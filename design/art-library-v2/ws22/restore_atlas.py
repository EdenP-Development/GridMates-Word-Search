#!/usr/bin/env python3
"""Losslessly restore the exact original WS22 motif atlas from GitHub blobs."""
from pathlib import Path
import hashlib, json
here=Path(__file__).resolve().parent
metadata=json.loads((here/"source_parts/manifest.json").read_text())
destination=here/metadata["source_image"]
blocks=[]
for part in metadata["shards"]:
    file=here/"source_parts"/part["filename"]
    chunk=file.read_bytes()
    if len(chunk)!=part["bytes"] or hashlib.sha256(chunk).hexdigest()!=part["sha256"]:
        raise RuntimeError("Missing or corrupted source shard: "+str(file))
    blocks.append(chunk)
data=b"".join(blocks)
if len(data)!=metadata["source_size_bytes"] or hashlib.sha256(data).hexdigest()!=metadata["source_sha256"]:
    raise RuntimeError("Reassembled WS22 source does not match expected SHA256")
destination.parent.mkdir(parents=True,exist_ok=True)
if destination.exists() and destination.read_bytes()!=data:
    raise RuntimeError("Refusing to overwrite a different master source image")
destination.write_bytes(data)
print(f"PASS: reconstructed exact WS22 atlas at {destination} ({len(data):,} bytes)")
