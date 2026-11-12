"""Export centered legacy/store icons from the same vector as the adaptive icon.

Requires Pillow. Run from any directory: python tools/export_launcher_icons.py
Only absolute M/L/H/V/Z polygon paths are supported; other commands fail explicitly.
"""

from pathlib import Path
import re
import xml.etree.ElementTree as ET

from PIL import Image, ImageColor, ImageDraw


ROOT = Path(__file__).resolve().parents[1]
ANDROID = "{http://schemas.android.com/apk/res/android}"
RES = ROOT / "app/src/main/res"
MARK = ROOT / "core/uikit/src/main/res/drawable/ic_tradebot_mark.xml"
DENSITIES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
SUPERSAMPLING = 4


def polygons(data):
    tokens = re.findall(r"[A-Za-z]|[-+]?(?:\d*\.\d+|\d+)", data)
    result, points = [], []
    index, x, y = 0, 0.0, 0.0
    while index < len(tokens):
        command = tokens[index]
        index += 1
        if command in ("M", "L"):
            x, y = float(tokens[index]), float(tokens[index + 1])
            index += 2
        elif command == "H":
            x = float(tokens[index])
            index += 1
        elif command == "V":
            y = float(tokens[index])
            index += 1
        elif command == "Z":
            result.append(points)
            points = []
            continue
        else:
            raise ValueError(f"Unsupported vector command: {command}")
        points.append((x, y))
    if points:
        raise ValueError("Brand paths must be closed")
    return result


def read_artwork():
    vector = ET.parse(MARK).getroot()
    viewport = (float(vector.attrib[ANDROID + "viewportWidth"]),
                float(vector.attrib[ANDROID + "viewportHeight"]))
    paths = [(ImageColor.getrgb(path.attrib[ANDROID + "fillColor"]), polygon)
             for path in vector.findall("path")
             for polygon in polygons(path.attrib[ANDROID + "pathData"])]
    all_points = [point for _, polygon in paths for point in polygon]
    center = ((min(x for x, _ in all_points) + max(x for x, _ in all_points)) / 2,
              (min(y for _, y in all_points) + max(y for _, y in all_points)) / 2)
    assert center == (viewport[0] / 2, viewport[1] / 2), "Center the shared vector first"
    return viewport, paths


def read_background():
    background = ET.parse(RES / "drawable/ic_launcher_background.xml").getroot()
    base = ImageColor.getrgb(background.find("path").attrib[ANDROID + "fillColor"])
    gradient = background.find(".//gradient")

    def argb(value):
        value = value.removeprefix("#")
        return tuple(int(value[i:i + 2], 16) for i in (2, 4, 6, 0))

    return base, argb(gradient.attrib[ANDROID + "startColor"]), argb(gradient.attrib[ANDROID + "endColor"])


def render(size, round_icon=False, store=False):
    viewport, paths = read_artwork()
    base, start, end = read_background()
    extent = size * SUPERSAMPLING
    margin = 0 if store else extent / 12
    bounds = (margin, margin, extent - margin - 1, extent - margin - 1)
    background = Image.new("RGBA", (extent, extent))
    draw = ImageDraw.Draw(background)
    for y in range(extent):
        fraction = min(1, max(0, (y - margin) / (extent - 2 * margin - 1)))
        rgba = [start[i] + (end[i] - start[i]) * fraction for i in range(4)]
        alpha = rgba[3] / 255
        color = tuple(round(base[i] * (1 - alpha) + rgba[i] * alpha) for i in range(3))
        draw.line((0, y, extent, y), fill=color + (255,))
    mask = Image.new("L", (extent, extent))
    mask_draw = ImageDraw.Draw(mask)
    if round_icon:
        mask_draw.ellipse(bounds, fill=255)
    else:
        mask_draw.rounded_rectangle(bounds, radius=0 if store else extent / 10, fill=255)
    background.putalpha(mask)

    # Adaptive launchers show the central 72dp of a 108dp layer. Use the same
    # foreground inset ratio, without copying any mark coordinates or colors.
    foreground = ET.parse(RES / "drawable/ic_launcher_foreground.xml").getroot()
    inset = foreground.find(".//inset").attrib[ANDROID + "inset"]
    assert inset.endswith("%"), "Exporter expects proportional adaptive padding"
    fraction = float(inset[:-1]) / 100
    mark_size = (extent - 2 * margin) * 108 / 72 * (1 - 2 * fraction)
    draw = ImageDraw.Draw(background)
    for color, polygon in paths:
        transformed = [(extent / 2 + (x / viewport[0] - 0.5) * mark_size,
                        extent / 2 + (y / viewport[1] - 0.5) * mark_size)
                       for x, y in polygon]
        draw.polygon(transformed, fill=color + (255,))
    image = background.resize((size, size), Image.Resampling.LANCZOS)
    # Verify every output, not just the largest source; allow antialiasing rounding.
    pixels = image.load()
    colored = [(x, y) for y in range(size) for x in range(size)
               if pixels[x, y][3] > 128 and
               (pixels[x, y][0] > 150 and pixels[x, y][1] > 200 and pixels[x, y][2] < 80
                or pixels[x, y][0] > 200 and pixels[x, y][1] < 100 and pixels[x, y][2] > 80)]
    box = (min(x for x, _ in colored), min(y for _, y in colored),
           max(x for x, _ in colored), max(y for _, y in colored))
    assert abs((box[0] + box[2]) / 2 - size / 2) <= 1, box
    assert abs((box[1] + box[3]) / 2 - size / 2) <= 1, box
    print(f"{size}x{size} {'round' if round_icon else 'square'}: mark bounds={box}")
    return image


if __name__ == "__main__":
    for density, size in DENSITIES.items():
        for round_icon in (False, True):
            suffix = "_round" if round_icon else ""
            render(size, round_icon).save(RES / f"mipmap-{density}/ic_launcher{suffix}.webp", lossless=True)
    render(512, store=True).save(ROOT / "app/src/main/ic_launcher-playstore.png")
