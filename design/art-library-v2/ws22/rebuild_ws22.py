#!/usr/bin/env python3
"""Rebuild GridMates WS22 motif sprites and premium frames from an atlas.
Requires Pillow, NumPy and SciPy. SVG masters are source text, independently editable.
This script does not modify the Android project.
"""
from pathlib import Path
from PIL import Image, ImageOps, ImageDraw, ImageFont
from scipy.ndimage import distance_transform_edt
import numpy as np, json, hashlib, shutil, zipfile, sys

SCRIPT=Path(__file__).resolve()
ROOT=SCRIPT.parent if (SCRIPT.parent/'sources').is_dir() and (SCRIPT.parent/'sprites').is_dir() else SCRIPT.parent / 'GridMates_Asset_Expansion_WS22'
SRC=ROOT/'sources'/'ws22_master_motifs_alpha.png'
PNG=ROOT/'sprites'/'motifs'; SVG=ROOT/'svg'/'premium_frames'; PREV=ROOT/'previews'
for p in (PNG,SVG,PREV,SRC.parent): p.mkdir(parents=True,exist_ok=True)
if not SRC.exists():
    origin=Path('/mnt/data/a_clean_illustration_sheet_with_a_transparent_chec.png')
    if not origin.exists():raise FileNotFoundError('Place the source atlas at sources/ws22_master_motifs_alpha.png')
    shutil.copy2(origin,SRC)

def digest(data):return hashlib.sha256(data).hexdigest()
master=Image.open(SRC).convert('RGBA'); W,H=master.size
names=[['woodland_badger','woodland_robin','woodland_squirrel','woodland_stag'],
       ['ocean_seahorse','ocean_jellyfish','ocean_lighthouse','ocean_pearl_clam'],
       ['garden_hummingbird','garden_watering_can','garden_sunflower','garden_mantis'],
       ['space_astronaut_helmet','space_telescope','space_comet','space_spiral_galaxy']]
entries=[]
for row in range(4):
  for col in range(4):
    name=names[row][col]
    x0=round(col*W/4); x1=round((col+1)*W/4)
    y0=round(row*H/4); y1=round((row+1)*H/4)
    arr=np.asarray(master.crop((x0,y0,x1,y1))).copy(); a=arr[:,:,3]
    # Alpha cleanup avoids saturated invisible RGB from generative transparencies
    # from leaking into resized premultiplied edges.
    clear=a<12; a[clear]=0
    # Ignore two tiny fragments from the next 4x4 atlas row that entered
    # the transparent padding below the Garden hummingbird/watering can.
    if row==2 and col in (0,1): a[-18:]=0
    opaque=a>=215
    if opaque.any():
      _,index=distance_transform_edt(~opaque,return_indices=True)
      nearest=arr[index[0],index[1],:3]
      arr[:,:,:3][a<215]=nearest[a<215]
    arr[:,:,3]=a
    ys,xs=np.where(a>28)
    if len(xs)==0:raise RuntimeError(name+' empty')
    pad=6
    bx0=max(0,int(xs.min())-pad);by0=max(0,int(ys.min())-pad)
    bx1=min(x1-x0,int(xs.max())+pad+1);by1=min(y1-y0,int(ys.max())+pad+1)
    cropped=Image.fromarray(arr,'RGBA').crop((bx0,by0,bx1,by1))
    dst=PNG/('gm_theme_'+name+'_motif.png');cropped.save(dst,optimize=True)
    entries.append({'id':'gm_theme_'+name+'_motif','scope':'theme','family':name.split('_')[0],
                    'name':name,'format':'PNG','status':'candidate_not_packaged',
                    'path':str(dst.relative_to(ROOT)),'sha256':digest(dst.read_bytes()),
                    'size_px':[cropped.width,cropped.height],
                    'source_cell':[row,col], 'source_crop_xyxy':[x0+bx0,y0+by0,x0+bx1,y0+by1],
                    'original_source':'sources/ws22_master_motifs_alpha.png'})

FINISHES={
'bronze':('#F3D6A8','#BD8142','#754726','#7C5734'),
'silver':('#FAF8E9','#ACBCC3','#5C7880','#546A72'),
'gold':('#FFF4BE','#DBB54C','#92601D','#6B6937'),
'emerald':('#E8E5AB','#78B17C','#1D664C','#27583C')}

def gradient(f):
 hi,mid,low,accent=FINISHES[f]
 return f'<defs><linearGradient id="m" x2="0" y2="1"><stop stop-color="{hi}"/><stop offset=".52" stop-color="{mid}"/><stop offset="1" stop-color="{low}"/></linearGradient><linearGradient id="p" x2="0" y2="1"><stop stop-color="#FFF7DF"/><stop offset="1" stop-color="#E7D1A9"/></linearGradient></defs>'

def embellishment(f):
 accent=FINISHES[f][3]
 return f'<g fill="{accent}" stroke="#354F34" stroke-width="1.2"><path d="M42 162q-26 -20 -19 -50q16 11 19 50zm15 11q-19 -32 -17 -45q17 8 17 45z"/><path d="M214 162q26 -20 19 -50q-16 11 -19 50zm-15 11q19 -32 17 -45q-17 8 -17 45z"/></g>'

def frame_svg(style,f):
 em=embellishment(f);g=gradient(f)
 if style=='round':
  shape='<circle cx="128" cy="128" r="115" fill="url(#m)" stroke="#64451B" stroke-width="5"/><circle cx="128" cy="128" r="97" fill="url(#p)" stroke="#FFF0BB" stroke-width="6"/><circle cx="128" cy="128" r="89" fill="none" stroke="#B79050" stroke-width="2"/>'
 elif style=='shield':
  shape='<path d="M128 8Q166 39 235 37V124Q237 192 128 247Q19 192 21 124V37Q90 39 128 8Z" fill="url(#m)" stroke="#60441E" stroke-width="5"/><path d="M128 26Q170 54 218 54V119Q218 181 128 227Q38 181 38 119V54Q86 54 128 26Z" fill="url(#p)" stroke="#FCE9B1" stroke-width="5"/>'
 elif style=='portrait':
  shape='<rect x="24" y="10" width="208" height="235" rx="54" fill="url(#m)" stroke="#60441E" stroke-width="5"/><rect x="39" y="25" width="178" height="205" rx="44" fill="url(#p)" stroke="#FBEAB7" stroke-width="4"/>'
 elif style=='ribbon':
  shape='<path d="M66 148L50 246L119 209L128 217L137 209L206 246L190 148" fill="url(#m)" stroke="#745022" stroke-width="5"/><circle cx="128" cy="100" r="91" fill="url(#m)" stroke="#715027" stroke-width="5"/><circle cx="128" cy="100" r="74" fill="url(#p)" stroke="#F9EABB" stroke-width="5"/>'
 elif style=='starburst':
  pts=[]
  import math
  for i in range(20):
   ang=(i*math.pi/10)-math.pi/2;rad=115 if i%2==0 else 92
   pts.append(f'{128+math.cos(ang)*rad:.1f},{128+math.sin(ang)*rad:.1f}')
  shape=f'<polygon points="{" ".join(pts)}" fill="url(#m)" stroke="#755326" stroke-width="5"/><circle cx="128" cy="128" r="80" fill="url(#p)" stroke="#FDE6AA" stroke-width="5"/>'
 else: # book crest
  shape='<path d="M128 54Q78 24 26 48V195Q88 169 128 204Q168 169 230 195V48Q178 24 128 54Z" fill="url(#m)" stroke="#745228" stroke-width="5"/><path d="M128 65Q82 43 41 61V177Q86 157 128 187Q170 157 215 177V61Q174 43 128 65Z" fill="url(#p)" stroke="#FCF0C8" stroke-width="4"/><path d="M128 65V187" stroke="#B68F4B" stroke-width="4"/>'
 return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256">{g}{shape}{em}<path d="M128 9l8 14-8 14-8-14Z" fill="#FFF2AB" stroke="#B78A3C" stroke-width="2"/></svg>\n'
for style in ['round','shield','portrait','ribbon','starburst','book_crest']:
 for finish in FINISHES:
  name=f'gm_core_prestige_{style}_{finish}'
  dst=SVG/(name+'.svg');dst.write_text(frame_svg(style,finish),encoding='utf-8')
  entries.append({'id':name,'scope':'core','family':'prestige_frames','name':style+'_'+finish,
                  'format':'SVG','status':'candidate_not_packaged','path':str(dst.relative_to(ROOT)),
                  'sha256':digest(dst.read_bytes()),'variant':finish,'master':style,
                  'source':'procedural scalable vector with exact palette tokens'})

manifest={'schema_version':1,'pack_id':'gridmates_ws22_theme_motifs_prestige','date':'2026-11-15',
          'purpose':'Cross-game reusable motif cutouts and collectible frames; no APK changes',
          'status':'candidate_only','counts':{'motif_pngs':16,'premium_frame_svgs':24,'total':40},
          'source_sheet':{'path':'sources/ws22_master_motifs_alpha.png','sha256':digest(SRC.read_bytes()),'size_px':[W,H]},
          'edge_notes':'Motif PNG alpha halos were decontaminated by nearest opaque color while keeping alpha coverage; visually review at actual 48-96 px. No live text baked in.',
          'entries':entries}
(ROOT/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
README='''# GridMates WS22 — Theme Motifs + Prestige Frames\n\n**Candidate-only:** 16 illustrated motif PNGs + 24 scalable SVG prestige frames (6 shapes × 4 finishes).\n\nMotifs: Woodland (badger, robin, squirrel, stag), Ocean (seahorse, jellyfish, lighthouse, pearl clam), Garden (hummingbird, watering can, sunflower, mantis), Space (astronaut helmet, telescope, comet, spiral galaxy).\n\nFrames: Round, Shield, Portrait, Ribbon, Starburst, Book Crest; Bronze, Silver, Gold, Emerald. These are generic GridMates Core templates, not tied to any in-game achievement or currency.\n\nArtwork was cropped from one transparent source atlas and cleaned for RGB contamination in translucent edge pixels. All 40 candidate assets are catalogued with exact SHA256. Text and unlock statuses belong to native UI.\n\nThese are **not yet installed in Word Search, Sudoku or any Android APK**. The painterly illustrations and individual SVG vector styles should be reviewed on 320dp/360dp and at 150–200% fonts before promotion.\n\nGitHub versioning may include SVG masters, metadata and the deterministic cropping script; full PNG atlas and PNG bitmap sources must be confirmed independently as a GitHub binary/archive backup or in ChatGPT Library.\n'''
(ROOT/'README.md').write_text(README,encoding='utf-8')
# Contact sheet: transparent white checkerboard preview + label in a separate box
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',20)
wcell,hcell=270,300
contact=Image.new('RGB',(4*wcell,4*hcell),(250,247,240));d=ImageDraw.Draw(contact)
for i,e in enumerate(entries[:16]):
 im=Image.open(ROOT/e['path']).convert('RGBA');im.thumbnail((244,248),Image.Resampling.LANCZOS)
 x=(i%4)*wcell+(wcell-im.width)//2;y=(i//4)*hcell+8
 # soft neutral bg not baked into sprite
 contact.paste(im,(x,y),im)
 d.text(((i%4)*wcell+14,(i//4)*hcell+266),e['name'].replace('_',' ').title(),font=font,fill=(39,70,51))
contact.save(PREV/'motifs_contact.jpg',quality=94)
# SVG previews rendered via cairosvg, for review only
try:
 import cairosvg
 sheet=Image.new('RGB',(6*210,4*240),(251,245,231));draw=ImageDraw.Draw(sheet)
 for i,e in enumerate(entries[16:]):
  rendered=cairosvg.svg2png(bytestring=(ROOT/e['path']).read_bytes(),output_width=180,output_height=180)
  import io
  im=Image.open(io.BytesIO(rendered)).convert('RGBA');x=(i%6)*210+15;y=(i//6)*240+5
  sheet.paste(im,(x,y),im);draw.text(((i%6)*210+8,(i//6)*240+197),e['name'].replace('_',' '),font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',12),fill=(35,78,50))
 sheet.save(PREV/'prestige_frames_contact.jpg',quality=93)
except ImportError:print('CairoSVG missing: SVG preview not generated')
# portable deterministic ZIP from ROOT, embeds rebuild script under tools
if SCRIPT.resolve() != (ROOT/'rebuild_ws22.py').resolve(): shutil.copy2(SCRIPT,ROOT/'rebuild_ws22.py')
zipout=ROOT.parent/'GridMates_Asset_Expansion_WS22.zip'
with zipfile.ZipFile(zipout,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for p in sorted(q for q in ROOT.rglob('*') if q.is_file()):
  name=p.relative_to(ROOT).as_posix(); info=zipfile.ZipInfo(name,date_time=(2026,1,1,0,0,0))
  info.compress_type=zipfile.ZIP_DEFLATED
  z.writestr(info,p.read_bytes(),compress_type=zipfile.ZIP_DEFLATED,compresslevel=9)
with zipfile.ZipFile(zipout) as z:
 assert z.testzip() is None
 assert len(z.namelist())==16+24+1+1+1+1+2, len(z.namelist())
 print('ZIP entries',len(z.namelist()))
print('Saved',zipout,'bytes',zipout.stat().st_size,'sha256',digest(zipout.read_bytes()))
print('Motifs',len(entries[:16]),'frames',len(entries[16:]),'sheet alpha',master.getchannel('A').getextrema())
