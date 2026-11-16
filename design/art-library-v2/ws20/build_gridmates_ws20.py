from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import io, json, hashlib, zipfile, math, shutil, re, os
import numpy as np
import cairosvg

ROOT=Path(os.environ.get('GRIDMATES_WS20_ROOT', str(Path(__file__).resolve().parents[1] if Path(__file__).parent.name=='tools' else Path('/mnt/data/GridMates_Asset_Expansion_WS20'))))
SRC=ROOT/'source'/'collection_reward_atlas_alpha.png'
REFS=[ROOT/'reference_only'/'concept_board_1.png',ROOT/'reference_only'/'concept_board_2.png']
for d in ['source','sprites/rewards','sprites/spines','svg/spines','previews','tools','reference_only']:(ROOT/d).mkdir(parents=True,exist_ok=True)
sha=lambda b:hashlib.sha256(b).hexdigest()
src=Image.open(SRC).convert('RGBA')
assert src.size==(1536,1024)
src.save(ROOT/'source'/'collection_reward_atlas_alpha.png',optimize=True)
# Generated sheet arranged as four rows of four independent objects, transparent elsewhere.
# Row-specific crop zones prevent the badge/medal highlights from being clipped.
cols=[(0,394),(394,788),(788,1094),(1094,1536)]
rows=[(0,307),(307,574),(574,837),(837,1024)]
# The star in row 3 begins at 551; its rightmost column has separate tighter bounds.
names=[
 ['portrait_oval_ribbon','profile_panel_blank','achievement_shield_blank','award_banner_wide'],
 ['locked_gold_seal','unlocked_gold_seal','equipped_check_seal','gold_star_wreath'],
 ['reward_sparkle_burst','completed_ribbon_seal','locked_silver_coin','champion_trophy_medal'],
 ['collection_section_plaque','button_forest_compact','milestone_progress_track','leaf_star_divider']
]
manifest={'schema_version':1,'pack_id':'gm_ws20_asset_expansion','created':'2026-11-15','status':'candidate_not_shipped',
  'story':'Pre-made reusable Word Search and GridMates Core assets with optional theme overlays.',
  'source_sheets':[],'sprites':[],'notes':['No images are installed into Android automatically.','All dynamic text and gameplay status must remain native and accessible.','72 book spines use deterministic vector templates; AI generated badges cropped pixel-exact from approved-style sheet.','Two labeled concept boards are reference-only, not transparent UI sprites.']}
sourcebytes=(ROOT/'source'/'collection_reward_atlas_alpha.png').read_bytes()
manifest['source_sheets'].append({'file':'source/collection_reward_atlas_alpha.png','sha256':sha(sourcebytes),'size_px':list(src.size),'alpha':True,'role':'exact_source_sprite_sheet'})
for ri,row in enumerate(names):
 for ci,name in enumerate(row):
  left,right=cols[ci];top,bottom=rows[ri]
  if ri==2 and ci==3:top=548
  if ri==1 and ci==3:bottom=546
  if ri==3 and ci==1:right=715
  if ri==3 and ci==2:left=715
  if ri==2 and ci==0:bottom=819
  if ri==3 and ci==0:top=819
  if ri==2 and ci==1:top=572
  # crop inside zone and then bound by actual nontransparent alpha
  tile=src.crop((left,top,right,bottom)); arr=np.asarray(tile.getchannel('A'))
  mask=arr>32
  ys,xs=np.where(mask)
  assert len(xs)>1000,(name,len(xs))
  pad=7;x0=max(0,int(xs.min())-pad);y0=max(0,int(ys.min())-pad)
  x1=min(tile.width,int(xs.max())+pad+1);y1=min(tile.height,int(ys.max())+pad+1)
  b=(left+x0,top+y0,left+x1,top+y1)
  im=src.crop(b)
  px=np.array(im);px[:,:,3][px[:,:,3]<27]=0
  im=Image.fromarray(px,'RGBA')
  out=ROOT/'sprites'/'rewards'/f'gm_core_{name}.png';im.save(out,optimize=True)
  manifest['sprites'].append({'id':f'gm_core_{name}','family':'core_rewards','path':out.relative_to(ROOT).as_posix(),
      'source':'source/collection_reward_atlas_alpha.png','source_crop_xyxy':list(b),'px':list(im.size),'sha256':sha(out.read_bytes()),
      'alpha':True,'status':'candidate','use':'Collection/Profile/Achievements/Rewards; native labels and state always required'})

# Reusable book spine system: 24 theme palettes × 3 variants, all independently editable source SVGs.
THEMES=[
 ('woodland','#234F35','#5D8246','leaf'),('ocean','#165660','#4A8D93','wave'),('bugs','#4D5330','#849047','butterfly'),('garden','#5C6C3C','#A5B46B','flower'),
 ('space','#25395E','#55679B','planet'),('winter','#597E96','#9CBCCF','snow'),('farm','#634C32','#BA9160','barn'),('spooky','#483252','#866383','pumpkin'),
 ('desert','#935B33','#CDA166','sun'),('night_sky','#192F56','#526DA1','moon'),('autumn','#814528','#C6884D','leaf'),('spring','#6D6E53','#B5AE8B','flower'),
 ('summer','#82613D','#DCB465','sun'),('mountains','#3C5356','#819999','mountain'),('dinosaurs','#476344','#8B9C5F','fossil'),('food','#8E5043','#C9906B','apple'),
 ('weather','#436C83','#8DA8B3','cloud'),('travel','#69513A','#AA8A59','compass'),('music','#4C3A5C','#8E70A6','music'),('fairy_tales','#75394C','#BD8091','crown'),
 ('tropical','#14665B','#5B9F7E','palm'),('arctic','#476B79','#91B3BD','snow'),('city','#454E5C','#8893A4','city'),('science','#445E66','#8CA7B1','flask')
]
# Each symbol sits inside a small gold medallion. All are geometry paths, never font-glyph emoji.
def motif(kind):
    p={
    'leaf':'<path d="M52 17 C21 27 22 58 48 83 C80 67 82 32 52 17Z"/><path d="M35 67L67 32M49 51L37 44M54 48L68 54" fill="none" stroke="#F8E6B4" stroke-width="5"/>',
    'wave':'<path d="M14 57Q28 25 43 42T78 39Q65 62 47 59T18 77Z"/><path d="M18 69Q45 43 78 60" fill="none" stroke="#F8E6B4" stroke-width="5"/>',
    'butterfly':'<path d="M49 45Q20 6 15 38Q15 59 42 62Q15 67 29 84Q46 89 49 62Q53 89 71 84Q85 67 58 62Q85 57 85 38Q80 6 51 45Z"/><path d="M50 35V79" stroke="#F8E6B4" stroke-width="5"/>',
    'flower':'<g><ellipse cx="50" cy="30" rx="13" ry="20"/><ellipse cx="70" cy="48" rx="13" ry="20" transform="rotate(65 70 48)"/><ellipse cx="60" cy="72" rx="13" ry="20" transform="rotate(135 60 72)"/><ellipse cx="34" cy="67" rx="13" ry="20" transform="rotate(210 34 67)"/><ellipse cx="30" cy="43" rx="13" ry="20" transform="rotate(285 30 43)"/></g><circle cx="50" cy="52" r="11" fill="#F8E6B4"/>',
    'planet':'<circle cx="48" cy="51" r="22"/><ellipse cx="49" cy="53" rx="40" ry="11" fill="none" stroke="#F8E6B4" stroke-width="6" transform="rotate(-19 49 53)"/>',
    'snow':'<path d="M50 13V87M13 50H87M24 24L76 76M76 24L24 76" fill="none" stroke="#F8E6B4" stroke-width="8"/><circle cx="50" cy="50" r="9"/>',
    'barn':'<path d="M18 44 50 18 82 44V84H18Z"/><path d="M39 84V52H62V84M23 44H78" fill="none" stroke="#F8E6B4" stroke-width="6"/>',
    'pumpkin':'<path d="M50 28Q76 19 82 48Q85 79 50 80Q15 79 18 48Q24 19 50 28Z"/><path d="M50 27Q42 14 49 11M41 39Q28 57 43 73M59 39Q72 57 57 73" fill="none" stroke="#F8E6B4" stroke-width="5"/>',
    'sun':'<circle cx="50" cy="50" r="20"/><path d="M50 9V22M50 78V91M9 50H22M78 50H91M21 21L31 31M69 69L79 79M79 21L69 31M31 69L21 79" fill="none" stroke="#F8E6B4" stroke-width="7"/>',
    'moon':'<path d="M69 17Q29 29 35 63Q42 80 73 71Q36 98 20 61Q11 26 69 17Z"/>',
    'mountain':'<path d="M10 81 37 31 51 53 64 18 92 81Z"/><path d="M27 53 37 31 47 52M55 44 64 18 78 49" fill="none" stroke="#F8E6B4" stroke-width="5"/>',
    'fossil':'<path d="M24 73Q8 52 29 32Q54 12 77 34Q92 52 70 68Q46 87 24 73Z"/><path d="M39 54Q56 40 72 54M49 58L45 71M58 56L60 70" fill="none" stroke="#F8E6B4" stroke-width="5"/>',
    'apple':'<path d="M49 31Q25 18 17 47Q16 79 43 83L54 78Q79 89 84 59Q91 18 59 30Z"/><path d="M49 30L54 14M54 24Q67 10 75 22" fill="none" stroke="#F8E6B4" stroke-width="5"/>',
    'cloud':'<path d="M25 72Q10 64 18 48Q23 38 38 39Q44 19 60 29Q74 29 75 43Q95 46 86 66Q82 73 68 73Z"/><path d="M40 80L33 92M60 80L53 92" fill="none" stroke="#F8E6B4" stroke-width="5"/>',
    'compass':'<circle cx="50" cy="50" r="32" fill="none" stroke="#F8E6B4" stroke-width="6"/><path d="M50 17 61 50 50 82 39 50Z"/><circle cx="50" cy="50" r="5" fill="#F8E6B4"/>',
    'music':'<path d="M58 20V67Q52 84 37 76Q30 66 42 62Q47 60 52 63V27L76 20V58Q69 73 57 68" fill="none" stroke="#F8E6B4" stroke-width="8"/>',
    'crown':'<path d="M15 37 31 56 49 19 67 56 86 37 77 80H23Z"/><path d="M25 67H75" fill="none" stroke="#F8E6B4" stroke-width="6"/>',
    'palm':'<path d="M49 38Q33 61 45 86" fill="none" stroke="#F8E6B4" stroke-width="7"/><path d="M49 38Q22 16 12 38Q32 34 49 43Q43 7 55 13Q61 23 54 43Q82 13 90 37Q66 33 54 46Q77 44 80 62Q62 55 51 47Z"/>',
    'city':'<path d="M17 82V41H38V82M39 82V23H61V82M62 82V49H85V82Z"/><path d="M23 54H32M23 68H32M46 35H54M46 50H54M46 65H54M69 60H77" fill="none" stroke="#F8E6B4" stroke-width="4"/>',
    'flask':'<path d="M37 15H64M43 15V44L21 78Q16 85 30 87H72Q84 85 78 76L57 44V15" fill="none" stroke="#F8E6B4" stroke-width="7"/><path d="M30 69Q48 59 68 69L75 80H25Z"/>',
    }[kind]
    return f'<g fill="#D4AF59" stroke="#805C24" stroke-width="3" stroke-linejoin="round" stroke-linecap="round">{p}</g>'

SVGGEN='''def function: make spine based on theme palette, ornaments and motif; stable SVG with no external assets, dynamic book labels painted in app.'''
def spine_svg(theme,dark,light,motif_kind,variant):
    regular=variant!='narrow'
    W=184 if regular else 120;H=760;cx=W/2
    medr=52 if regular else 39
    inner=W-38
    bandx=17 if regular else 13
    topband=72 if regular else 64
    margin=9 if regular else 7
    title_y=461
    collector = variant=='collector'
    extra = (f'<path d="M{margin+21} 132Q{margin+3} 160 {margin+21} 200M{W-margin-21} 132Q{W-margin-3} 160 {W-margin-21} 200M{margin+21} 566Q{margin+3} 589 {margin+21} 615M{W-margin-21} 566Q{W-margin-3} 589 {W-margin-21} 615" stroke="#F8D684" fill="none" stroke-width="4"/><path d="M{cx} 250l10 11-10 11-10-11Z M{cx} 570l10 11-10 11-10-11Z" fill="#F8E1A0" stroke="#9C7135" stroke-width="2"/><circle cx="{cx}" cy="196" r="8" fill="#EAC77D"/>') if collector else ''
    # material grain uses vector lines at very low opacity, not raster noise.
    grain=''.join(f'<path d="M{margin+6+(k*19)%(W-2*margin-12)} {96+(k*59)%545}v{8+(k*3)%29}" stroke="#FFF6C8" stroke-width="1" opacity=".07"/>' for k in range(35))
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {W} {H}" aria-label="Decorative {theme} book spine without text"><defs>
<linearGradient id="leather" x1="0" x2="1"><stop stop-color="#101A18"/><stop offset=".16" stop-color="{dark}"/><stop offset=".43" stop-color="{light}"/><stop offset=".68" stop-color="{dark}"/><stop offset="1" stop-color="#111A19"/></linearGradient>
<linearGradient id="gold" x1="0" x2=".12" y1="0" y2="1"><stop stop-color="#F8E6AD"/><stop offset=".27" stop-color="#D9B362"/><stop offset=".55" stop-color="#A8792F"/><stop offset=".9" stop-color="#E5C779"/><stop offset="1" stop-color="#8C612C"/></linearGradient>
<radialGradient id="seal"><stop stop-color="#FBEBBC"/><stop offset="1" stop-color="#D3A953"/></radialGradient></defs>
<rect x="7" y="7" width="{W-14}" height="746" rx="13" fill="#312518" opacity=".55"/>
<rect x="{margin}" y="{margin}" width="{W-2*margin}" height="{H-2*margin}" rx="14" fill="url(#leather)" stroke="#614522" stroke-width="4"/>
<rect x="{margin+7}" y="{margin+7}" width="{W-2*(margin+7)}" height="{H-2*(margin+7)}" rx="8" fill="none" stroke="#E5C478" stroke-width="2.7"/>
<path d="M{bandx} 95H{W-bandx} M{bandx} 112H{W-bandx} M{bandx} 635H{W-bandx} M{bandx} 652H{W-bandx}" stroke="#D5AA54" stroke-width="7"/><path d="M{bandx} 95H{W-bandx} M{bandx} 112H{W-bandx} M{bandx} 635H{W-bandx} M{bandx} 652H{W-bandx}" stroke="#FFE6A0" stroke-width="2"/>
<rect x="{margin}" y="{margin}" width="{W-2*margin}" height="{topband}" rx="13" fill="url(#gold)" stroke="#795222" stroke-width="3"/>
<rect x="{margin}" y="{H-81}" width="{W-2*margin}" height="{81-margin}" rx="13" fill="url(#gold)" stroke="#795222" stroke-width="3"/>
<path d="M{margin+18} 40H{W-margin-18} M{margin+18} {H-46}H{W-margin-18}" stroke="#FFF0C7" stroke-width="3" opacity=".7"/>
{grain}
<path d="M{cx} 148l10 12-10 12-10-12Z M{cx} 600l10 12-10 12-10-12Z" fill="#E7C471" stroke="#89642A" stroke-width="2"/>
<path d="M{cx} 195v34M{cx} 532v36" stroke="#D2AB5C" stroke-width="3"/>
<circle cx="{cx}" cy="335" r="{medr+8}" fill="#1A2B20" opacity=".4"/>
<circle cx="{cx}" cy="331" r="{medr+3}" fill="url(#gold)" stroke="#7A5122" stroke-width="3"/>
<circle cx="{cx}" cy="331" r="{medr-5}" fill="{dark}" stroke="#FAE1A3" stroke-width="2"/>
<g transform="translate({cx-(medr-14)} {331-(medr-14)}) scale({2*(medr-14)/100})">{motif(motif_kind)}</g>
<rect x="{margin+19}" y="{title_y}" width="{W-2*(margin+19)}" height="98" rx="9" fill="{dark}" stroke="#D6AA5C" stroke-width="3"/>
<path d="M{margin+25} 475H{W-margin-25} M{margin+25} 542H{W-margin-25}" stroke="#E9C878" stroke-width="1.5" opacity=".9"/>
<circle cx="{cx}" cy="211" r="5" fill="#D9B45B"/><circle cx="{cx}" cy="594" r="5" fill="#D9B45B"/>
{extra}
</svg>'''

for theme,dark,light,motif_kind in THEMES:
  for width in ['regular','narrow','collector']:
    name=f'gm_ws_spine_{theme}_{width}'
    svg=spine_svg(theme,dark,light,motif_kind,width)
    path=ROOT/'svg'/'spines'/f'{name}.svg';path.write_text(svg,encoding='utf-8')
    png=ROOT/'sprites'/'spines'/f'{name}.png'; scale=2.4
    cairosvg.svg2png(bytestring=svg.encode(),write_to=str(png),output_width=round((184 if width=='regular' else 120)*scale),output_height=round(760*scale))
    im=Image.open(png)
    assert im.getchannel('A').getextrema()[0]==0,(name,im.getpixel((0,0)))
    manifest['sprites'].append({'id':name,'family':'word_search_spines','path':png.relative_to(ROOT).as_posix(),
          'source':path.relative_to(ROOT).as_posix(),'source_sha256':sha(path.read_bytes()),'px':list(im.size),'sha256':sha(png.read_bytes()),
          'alpha':True,'status':'candidate','theme':theme,'width':width,'use':'Blank customizable bookshelf spine; title text dynamically rendered in app'})

for index,p in enumerate(REFS,1):
 if p.exists():
  d=ROOT/'reference_only'/f'concept_board_{index}.png'
  if p.resolve()!=d.resolve():shutil.copyfile(p,d)
  manifest['source_sheets'].append({'file':d.relative_to(ROOT).as_posix(),'sha256':sha(d.read_bytes()),'role':'reference_only_opaque_board_not_sprite'})

# Build transparent-aware contact sheet images; labels are in previews only.
font='/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf';bf='/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf'
F=lambda n,b=False:ImageFont.truetype(bf if b else font,n)
def checker(image):
 d=ImageDraw.Draw(image)
 for y in range(0,image.height,20):
  for x in range(0,image.width,20):
   d.rectangle((x,y,x+19,y+19),fill='#EFEBDD' if ((x//20+y//20)%2==0) else '#E4E1D7')

def make_sheet(subset,title,fn,cols,width,height):
 rows_n=math.ceil(len(subset)/cols); margin=32; header=115
 can=Image.new('RGB',(cols*width+margin*2,rows_n*height+header+margin),'#ECEAE1')
 d=ImageDraw.Draw(can);d.text((margin,18),title,fill='#294C3B',font=F(32,True));d.text((margin,64),'Candidate source art · isolated transparent PNG assets · not in the Android APK',font=F(17),fill='#5D6C5E')
 for i,item in enumerate(subset):
  x=margin+(i%cols)*width; y=header+(i//cols)*height
  d.rounded_rectangle((x+5,y+4,x+width-7,y+height-7),radius=15,fill='#FDFBF4',outline='#C8BB9D',width=2)
  img=Image.open(ROOT/item['path']).convert('RGBA')
  sx=min((width-24)/img.width,(height-67)/img.height)
  img=img.resize((max(1,int(img.width*sx)),max(1,int(img.height*sx))),Image.Resampling.LANCZOS)
  px=x+(width-img.width)//2;py=y+12+(height-73-img.height)//2
  can.paste(img,(px,py),img)
  lab=item['id'].replace('gm_core_','').replace('gm_ws_spine_','')
  if len(lab)>22:lab=lab[:22]+'…'
  d.text((x+13,y+height-41),lab,fill='#304D3A',font=F(18))
 can.save(ROOT/'previews'/fn,quality=90)

reward_items=[x for x in manifest['sprites'] if x['family']=='core_rewards']
spine_items=[x for x in manifest['sprites'] if x['family']=='word_search_spines']
make_sheet(reward_items,'GridMates / Collection & Reward Reserve — 16 pieces','rewards_contact_sheet.jpg',4,360,260)
make_sheet(spine_items,'GridMates / Word Search Theme Book Spines — 72 pieces','spines_contact_sheet.jpg',9,180,300)

# quick cross-check exact source crop and determinism. SVGs rerender pixel-exact at fixed render size.
for item in reward_items:
  assert (ROOT/item['path']).exists()
  im=Image.open(ROOT/item['path']); assert im.getchannel('A').getextrema()[0]==0
for item in spine_items:
  src=ROOT/item['source'];render=cairosvg.svg2png(bytestring=src.read_bytes(),output_width=item['px'][0],output_height=item['px'][1]);im=Image.open(io.BytesIO(render));assert sha(render)==item['sha256'],item['id']
manifest['counts']={'collection_reward_sprites':len(reward_items),'theme_spine_sprites':len(spine_items),'total_candidate_sprites':len(manifest['sprites']),'themes':len(THEMES)}
(ROOT/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
(ROOT/'README.md').write_text('''# GridMates Expansion WS20 — Collection, Rewards, and 72 Book Spines\n\n88 **real prepared transparent PNG** candidates: 16 cropped from a new original painterly source sheet, and 72 generated from deterministic standalone editable SVG source files (24 thematic palettes × narrow/regular/collector). Each includes a semantic ID, sha256, pixel size and source reference in manifest.json. Two design boards with embedded text are REFERENCE ONLY and must never be used as live sprites.\n\nSource art bank only — not Android-integrated or owner-accepted in-app. Character portrait IDs/save data/multiplayer are unaffected. Dynamic text/state is rendered natively. During Android integration, do not stretch atlas PNG borders without true nine-slice handling; prefer the editable spine SVGs for scalable assets.\n\nContents:\n- source/ original transparent Collection/Rewards source sheet\n- sprites/rewards/ 16 individually isolated PNGs\n- svg/spines/ 72 deterministic editable book spine SVG source files\n- sprites/spines/ 72 rendered high-resolution transparent PNGs\n- previews/ contact sheets with labels OUTSIDE the sprites\n- reference_only/ concept design boards (opaque, text-bearing, not runtime resources)\n- manifest.json checksums, provenance, status\n- tools/build_gridmates_ws20.py reproducible build source\n\nWhat next: preview Collection/Profile and Library on 320/360dp, 200% text, keep puzzle board unchanged and test install-over. Nothing in this archive automatically modifies app source or a release.\n''',encoding='utf-8')
target=ROOT/'tools'/'build_gridmates_ws20.py'
if Path(__file__).resolve()!=target.resolve():shutil.copyfile(Path(__file__),target)
pack=ROOT.parent/(ROOT.name+'.zip')
with zipfile.ZipFile(pack,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as zipf:
 for path in ROOT.rglob('*'):
  if path.is_file():zipf.write(path,path.relative_to(ROOT).as_posix())
with zipfile.ZipFile(pack) as zipf:
 assert zipf.testzip() is None
 m=json.loads(zipf.read('manifest.json'))
 for s in m['sprites']:
  assert sha(zipf.read(s['path']))==s['sha256']
print('ZIP',pack,pack.stat().st_size,sha(pack.read_bytes()))
print('MANIFEST',manifest['counts'])
print('EXAMPLE SVG',ROOT/'svg'/'spines'/'gm_ws_spine_woodland_regular.svg')