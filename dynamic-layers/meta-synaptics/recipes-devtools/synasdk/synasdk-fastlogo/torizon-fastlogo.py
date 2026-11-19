#!/usr/bin/env python3
# Replace the pictures in a Synaptics fastlogo.subimg.gz, keeping its header.
#
# Layout: u32 version, u32 count, then count x {u32 offset, width, height,
# stride} (little endian), each picture UYVY (BT.601 limited range) at offset.
# Usage: torizon-fastlogo.py <logo.png> <fastlogo.subimg.gz>  (edited in place)

import gzip
import struct
import sys

from PIL import Image


def uyvy(logo, w, h):
    # Centre the logo on black, at most half the screen width.
    scale = min(1.0, w / 2 / logo.width)
    lw, lh = int(logo.width * scale), int(logo.height * scale)
    canvas = Image.new("RGB", (w, h))
    canvas.paste(logo.resize((lw, lh), Image.LANCZOS), ((w - lw) // 2, (h - lh) // 2),
                 logo.resize((lw, lh), Image.LANCZOS))
    y, cb, cr = canvas.convert("YCbCr").split()  # full range
    y = y.point(lambda v: 16 + v * 219 // 255)
    cb, cr = (c.resize((w // 2, h), Image.BOX).point(lambda v: 128 + (v - 128) * 224 // 255)
              for c in (cb, cr))
    out = bytearray(w * h * 2)
    yb = y.tobytes()
    out[0::4], out[1::4], out[2::4], out[3::4] = cb.tobytes(), yb[0::2], cr.tobytes(), yb[1::2]
    return out


def main(png, blob_path):
    logo = Image.open(png).convert("RGBA")
    blob = bytearray(gzip.open(blob_path).read())
    _, count = struct.unpack_from("<II", blob)
    for i in range(count):
        off, w, h, stride = struct.unpack_from("<4I", blob, 8 + 16 * i)
        assert stride == w * 2, f"picture {i}: stride {stride} is not UYVY for width {w}"
        blob[off:off + w * h * 2] = uyvy(logo, w, h)
    with gzip.open(blob_path, "wb") as f:
        f.write(blob)


if __name__ == "__main__":
    main(*sys.argv[1:])
