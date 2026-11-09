#!/usr/bin/env python3
"""Generate the Gravity Boots textures.

Pure stdlib PNG writer so this runs anywhere without Pillow.
Run from the repo root:  python3 tools/make_textures.py
"""

import os
import struct
import zlib

W = H = 16

# Palette shorthand used by the pixel maps below.
#   .  transparent
#   k  dark outline
#   i  iron grey (dark / mid / light)
#   j
#   l
#   e  ender purple (dark / mid / bright)
#   f
#   g
COLORS = {
    ".": (0, 0, 0, 0),
    "k": (24, 20, 34, 255),
    "i": (88, 92, 104, 255),
    "j": (126, 132, 148, 255),
    "l": (176, 182, 198, 255),
    "e": (58, 26, 92, 255),
    "f": (122, 58, 186, 255),
    "g": (188, 130, 255, 255),
}

# 16x16 item icon: a pair of boots, seen from the side, with an ender-purple
# sole that is doing the actual sticking.
ITEM = [
    "................",
    "....kkkk........",
    "...kljjkk.......",
    "...kljjik.......",
    "...kljjik.......",
    "...kljjik.......",
    "...kljjik.......",
    "...kljjikkkk....",
    "...kljjijjjlk...",
    "...kljjijjjjk...",
    "...kliiijjjjk...",
    "...kkeeeeeeekk..",
    "...kefffffffek..",
    "...kegggggggek..",
    "...kkeeeeeeekk..",
    "................",
]


def write_png(path, rows):
    """Write an RGBA PNG from a list of 'pixel map' strings."""
    raw = bytearray()
    for row in rows:
        raw.append(0)  # PNG filter type 0 (None) for each scanline
        for ch in row:
            raw.extend(COLORS[ch])

    def chunk(tag, data):
        out = struct.pack(">I", len(data)) + tag + data
        return out + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    header = struct.pack(">IIBBBBB", W, H, 8, 6, 0, 0, 0)  # 8-bit RGBA
    png = (
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", header)
        + chunk(b"IDAT", zlib.compress(bytes(raw), 9))
        + chunk(b"IEND", b"")
    )

    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as fh:
        fh.write(png)
    print("wrote", path)


if __name__ == "__main__":
    base = "src/main/resources/assets/gravity_boots/textures"
    write_png(os.path.join(base, "item", "gravity_boots.png"), ITEM)
