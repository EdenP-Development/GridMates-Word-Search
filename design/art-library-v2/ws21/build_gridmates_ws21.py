#!/usr/bin/env python3
"""GridMates WS21 reproducible preproduction pack.

Input: generated 4x4 seasonal/reward atlas RGB and two opaque reference boards.
Output: 16 cleaned alpha illustrative candidates, 72 editable thematic cover
SVG+PNG variants, 24 medallions SVG+PNG, 24 functional UI master
SVG+PNG variants, README/manifest/contact sheets/source archive.

All output art is CANDIDATE, never Android runtime resources automatically.
"""
from __future__ import annotations
from PIL import Image, ImageDraw, ImageFont
from pathlib import Path
import numpy as np
import cv2, cairosvg
import hashlib, json, io, shutil, zipfile, textwrap

HERE=Path(__file__).resolve()
ROOT=HERE.parents[1] if HERE.parent.name=='tools' else HERE.parent/'GridMates_Asset_Expansion_WS21'
BASE=ROOT.parent
SRC=ROOT/'sources'; SP=ROOT/'sprites'; SV=ROOT/'svg'; PRE=ROOT/'previews'
for p in (SRC,SP,SV,PRE):p.mkdir(parents=True,exist_ok=True)
source_path=(SRC/'ws21_illustrated_reward_seasonal_atlas_rgb.png' if (SRC/'ws21_illustrated_reward_seasonal_atlas_rgb.png').exists() else BASE/'a_clean_graphic_illustration_sheet_on_a_transparen.png')
reference_paths=[(SRC/f'ws21_reference_board_{i}.png' if (SRC/f'ws21_reference_board_{i}.png').exists() else BASE/n) for i,n in enumerate(['a_highly_detailed_polished_colorful_ui_asset_ma.png','a_highly_detailed_polished_illustrated_game_asset.png'],1)]
if source_path.resolve()!=(SRC/'ws21_illustrated_reward_seasonal_atlas_rgb.png').resolve():
    shutil.copy2(source_path,SRC/'ws21_illustrated_reward_seasonal_atlas_rgb.png')
for n,p in enumerate(reference_paths,1):
    dst=SRC/f'ws21_reference_board_{n}.png'
    if p.resolve()!=dst.resolve():shutil.copy2(p,dst)

font_candidates=['/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf','/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf']
fontfile=next((p for p in font_candidates if Path(p).exists()),None)

def font(size):return ImageFont.truetype(fontfile,size) if fontfile else ImageFont.load_default()
def sha(p):return hashlib.sha256(Path(p).read_bytes()).hexdigest()

def out_png(im,path):
    path.parent.mkdir(parents=True,exist_ok=True)
    im.save(path,'PNG')

def render(svg_text,png_path,width=420):
    png=cairosvg.svg2png(bytestring=svg_text.encode(),output_width=width)
    png_path.parent.mkdir(parents=True,exist_ok=True)
    png_path.write_bytes(png)

def record(id,scope,family,file,source=None,details=None):
    im=Image.open(file)
    assert im.mode=='RGBA',id
    a=np.asarray(im.getchannel('A'))
    assert a.min()==0 and a.max()>200,(id,a.min(),a.max())
    obj={'id':id,'scope':scope,'family':family,'path':file.relative_to(ROOT).as_posix(),
       'width_px':im.width,'height_px':im.height,'sha256':sha(file),
       'has_alpha':True,'status':'candidate','source':source,'details':details or {}}
    manifest['sprites'].append(obj)

manifest={'schema_version':1,'pack_id':'gm_ws21_covers_seasons_core_reserve',
    'date':'2026-11-15','status':'candidate_not_shipped',
    'description':'New seasonal reward art, adaptable themed book covers, theme medallions, and neutral functional GridMates Core UI template graphics',
    'source_sheets':[],'sprites':[], 'notes':[
        '16 illustrated sprites separated from an RGB atlas with baked near-white checkerboard background using color-aware alpha removal; review edge quality manually before production.',
        'Two generated visual overview boards have baked text and are reference-only, not runtime sprites.',
        'Book-cover SVGs and medallion/UI SVGs can regenerate PNG candidates; app labels, titles, buttons and status text stay native.',
        'Do not ship all reserve PNGs by default, and do not change book IDs, profiles, progress or board gestures.'
    ]}
for p in sorted(SRC.iterdir()):
    manifest['source_sheets'].append({'path':p.relative_to(ROOT).as_posix(),'sha256':sha(p),
        'role':'illustration_atlas_rgb_cleanup_needed' if 'atlas_rgb' in p.name else 'reference_only_baked_text_not_sprite'})

# 16 cutouts from 4x4 image with high-key checkerboard erased. Exact source box recorded.
atlas=np.asarray(Image.open(source_path).convert('RGB')).astype(np.uint8)
H,W=atlas.shape[:2]
reward_names=[
'round_laurel_blank','shield_laurel_blank','parchment_ribbon_blank','open_book_laurel_crest',
'autumn_leaves_acorn','winter_snow_pine','spring_blossoms','summer_sunflower_bee',
'sealed_letter','gold_trophy_cup','gold_ornate_key','gift_box_green',
'round_award_blank','gold_star_wreath','bookmark_blank_green','square_parchment_frame']
for i,name in enumerate(reward_names):
    row,col=divmod(i,4)
    x0=round(col*W/4); x1=round((col+1)*W/4);y0=round(row*H/4);y1=round((row+1)*H/4)
    # The source illustration intentionally extends four badge ribbons
    # beyond the atlas row border. Restore that bleed from adjacent empty
    # margin rather than chopping the bow/bookmark tips.
    if row==0 and col in (0,1):y1=min(H,y1+28)
    if row==3 and col in (0,2):y0=max(0,y0-28)
    rgb=atlas[y0:y1,x0:x1].astype(np.float32)
    lower=np.min(rgb,axis=2); upper=np.max(rgb,axis=2)
    # White and neutral-grey checkerboard is close to grayscale near #F8F8F8.
    # Yellowish paper and all colored art have greater channel separation.
    chroma=upper-lower
    al=np.clip(np.maximum((242-lower)/12, (chroma-3)/17),0,1)
    al[ (lower>=245)&(chroma<9) ]=0
    mask=(al>0.08).astype('uint8')
    count, lab, stats,_=cv2.connectedComponentsWithStats(mask,8)
    keep=np.zeros(mask.shape,np.uint8)
    largest=max((int(stats[z,cv2.CC_STAT_AREA]) for z in range(1,count)),default=0)
    for z in range(1,count):
        # Kill stray pieces crossing neighboring atlas rows while preserving
        # naturally separated secondary motifs such as the summer bee.
        if int(stats[z,cv2.CC_STAT_AREA])>=max(1000,int(largest*.018)):
            keep[lab==z]=1
    # Tiny 1px seam edge smoothing; don't wash out paper interior.
    al=al*keep
    al=cv2.GaussianBlur(al.astype('float32'),(3,3),0.35)
    al=np.where(al<0.09,0,al)
    alpha=(np.clip(al,0,1)*255).astype(np.uint8)
    yy,xx=np.nonzero(alpha>26)
    if not len(xx):raise RuntimeError('Empty crop '+name)
    pad=12
    l=max(0,int(xx.min())-pad);rr=min(x1-x0,int(xx.max())+pad+1)
    t=max(0,int(yy.min())-pad);bb=min(y1-y0,int(yy.max())+pad+1)
    # Anything crossing atlas tile edges needs additional manual review.
    edge={'left':int(xx.min()),'right':int(x1-x0-1-xx.max()),'top':int(yy.min()),'bottom':int(y1-y0-1-yy.max())}
    rgba=np.concatenate([rgb.astype(np.uint8),alpha[:,:,None]],axis=2)[t:bb,l:rr]
    # RGBA only, no baked white around the cropped artwork.
    out=SP/'illustrated'/f'gm_core_ws21_{name}.png'
    out_png(Image.fromarray(rgba,'RGBA'),out)
    record('gm_core_ws21_'+name,'core','illustrated_rewards',out,
        'sources/ws21_illustrated_reward_seasonal_atlas_rgb.png',
        {'grid_row':row,'grid_col':col,'source_box_xyxy':[x0+l,y0+t,x0+rr,y0+bb],
         'edge_margin_pixels':edge,'cleanup':'neutral_checkerboard_color_key_v1'})

# Pure deterministic resizable vector templates. Theme palettes and emblems can
# be swapped independently. No baked readable text inside any visual asset.
THEMES={
'woodland':('#244B34','#89A75B','leaf'), 'ocean':('#276879','#91C9CE','wave'),
'bugs':('#6B6041','#D9A15C','butterfly'), 'garden':('#3E7654','#DD9FB0','flower'),
'space':('#262D63','#E1A461','planet'), 'winter':('#386C83','#CFE5EB','snow'),
'farm':('#864B31','#E4BD64','sun'), 'spooky':('#533559','#DA8D35','moon'),
'desert':('#B06B3D','#ECD297','sun'), 'night_sky':('#283D68','#A6B7DB','star'),
'autumn':('#8F4B32','#E4B15B','leaf'), 'spring':('#72875A','#E6B9CC','flower'),
'summer':('#D18B33','#F5D88A','sun'), 'mountains':('#486B6B','#AEC8C2','mountain'),
'dinosaurs':('#586F42','#D4BC7D','footprint'), 'food':('#9B6749','#F6C8A7','heart'),
'weather':('#4C7990','#CCDCEB','cloud'), 'travel':('#486C67','#DBC690','compass'),
'music':('#6F4369','#E1B5C9','music'), 'fairy_tales':('#694D7F','#DDC496','star'),
'arctic':('#4D8295','#DBEDF0','snow'), 'city':('#535C68','#CBA86E','house'),
'underwater':('#146D77','#81C9CA','wave'), 'tropical':('#26735E','#E9CE6C','leaf')}
assert len(THEMES)==24

def motif(kind, cx,cy,s=1.0,stroke='#FBE8B5',fill='#EFCE76'):
    # All path coordinates relative to a 100x100 motif space.
    base={
    'leaf':'<path d="M51 89Q12 42 78 13Q95 63 51 89Z"/><path d="M50 86Q55 49 78 20" fill="none"/>',
    'wave':'<path d="M5 59Q22 24 46 58Q62 83 92 45Q91 90 56 94Q31 98 5 59Z"/>',
    'butterfly':'<path d="M49 45C7 0 -5 66 36 67C2 110 50 102 49 57C48 106 98 109 65 67C104 58 99 3 51 45Z"/><path d="M50 33V78" fill="none"/>',
    'flower':'<g><ellipse cx="50" cy="28" rx="14" ry="25"/><ellipse cx="50" cy="72" rx="14" ry="25"/><ellipse cx="28" cy="50" rx="25" ry="14"/><ellipse cx="72" cy="50" rx="25" ry="14"/><circle cx="50" cy="50" r="13" fill="#E2B149"/></g>',
    'planet':'<circle cx="51" cy="47" r="26"/><ellipse cx="50" cy="51" rx="46" ry="12" transform="rotate(-22 50 51)" fill="none" stroke-width="7"/>',
    'snow':'<path d="M50 6V94M12 28L88 72M12 72L88 28M41 18L50 7L59 18M41 82L50 93L59 82M16 39L12 28L25 27M75 73L88 72L84 61" fill="none" stroke-width="9"/>',
    'sun':'<circle cx="50" cy="50" r="23"/><path d="M50 5V18M50 82V95M5 50H18M82 50H95M18 18L28 28M72 72L82 82M18 82L28 72M72 28L82 18" stroke-width="8" fill="none"/>',
    'moon':'<path d="M74 9A42 42 0 1 0 88 73A38 38 0 0 1 74 9Z"/>',
    'star':'<path d="M50 7L61 36L92 38L68 57L78 88L50 70L22 88L32 57L8 38L39 36Z"/>',
    'mountain':'<path d="M5 88L36 17L58 56L71 32L95 88Z"/><path d="M24 47L36 17L49 44M62 54L71 32L80 50" fill="none"/>',
    'footprint':'<path d="M22 76Q20 48 49 54Q75 57 77 83Q44 94 22 76Z"/><circle cx="20" cy="45" r="9"/><circle cx="38" cy="30" r="10"/><circle cx="58" cy="28" r="10"/><circle cx="78" cy="42" r="8"/>',
    'heart':'<path d="M50 88L17 56C-3 32 24 10 49 35C75 9 104 32 82 57Z"/>',
    'cloud':'<path d="M19 78Q5 76 5 59Q4 42 24 39Q28 8 58 24Q72 17 82 36Q96 39 95 58Q94 79 73 78Z"/>',
    'compass':'<circle cx="50" cy="50" r="36"/><path d="M50 10L60 40L90 50L60 60L50 90L40 60L10 50L40 40Z" fill="#E1B85D"/>',
    'music':'<path d="M44 18L83 10V63M44 18V77M83 10V60" fill="none" stroke-width="12"/><ellipse cx="29" cy="78" rx="18" ry="11"/><ellipse cx="68" cy="62" rx="18" ry="11"/>',
    'house':'<path d="M12 48L50 13L88 48V89H12Z"/><path d="M39 89V58H61V89M26 58H32M68 58H74" fill="none"/>',
    }
    body=base[kind]
    return f'<g transform="translate({cx-50*s:.2f} {cy-50*s:.2f}) scale({s:.3f})" fill="{fill}" stroke="{stroke}" stroke-width="4.5" stroke-linejoin="round" stroke-linecap="round">{body}</g>'

DEF='''<defs><linearGradient id="metal" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#F9E29C"/><stop offset=".5" stop-color="#BC8B37"/><stop offset="1" stop-color="#8B6229"/></linearGradient><linearGradient id="paper" x1="0" y1="0" x2=".3" y2="1"><stop stop-color="#FFF5E4"/><stop offset="1" stop-color="#E8D2A9"/></linearGradient><linearGradient id="cloth" x1="0" y1="0" x2="1" y2="0"><stop stop-color="#12291E"/><stop offset=".22" stop-color="COLOR"/><stop offset=".82" stop-color="COLOR"/><stop offset="1" stop-color="#142B25"/></linearGradient></defs>'''

def leaf(cx,cy,angle=0,size=1):
    return f'<g transform="translate({cx} {cy}) rotate({angle}) scale({size})"><path d="M0 0C9 -14 25 -16 33 -17C26 0 10 6 0 0Z" fill="#738B50" stroke="#4B6336" stroke-width="1.5"/><path d="M0 0C-8 -12 -9 -25 -8 -34C7 -23 10 -8 0 0Z" fill="#82955A" stroke="#4B6336" stroke-width="1.5"/></g>'

def wrapsvg(w,h,body,base_color='#254A38'):
    return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {w} {h}">'+DEF.replace('COLOR',base_color)+body+'</svg>\n'

for theme,(primary,accent,kind) in THEMES.items():
    for variant in ['standard','collector','pocket']:
        w,h={'standard':(480,660),'collector':(500,690),'pocket':(370,610)}[variant]
        margin=16 if variant=='collector' else 12
        mid=w/2
        body=f'<rect x="{margin}" y="{margin}" width="{w-2*margin}" height="{h-2*margin}" rx="24" fill="url(#metal)" stroke="#7F5422" stroke-width="5"/>'
        body+=f'<rect x="{margin+10}" y="{margin+10}" width="{w-2*margin-20}" height="{h-2*margin-20}" rx="19" fill="url(#cloth)" stroke="#F6D78E" stroke-width="4"/>'
        body+=f'<rect x="{margin+24}" y="{margin+24}" width="{w-2*margin-48}" height="{h-2*margin-48}" rx="14" fill="none" stroke="{accent}" stroke-width="3"/>'
        if variant=='collector':
            body+=f'<rect x="{margin+19}" y="{margin+19}" width="{w-2*margin-38}" height="{h-2*margin-38}" rx="17" fill="none" stroke="#F0D68F" stroke-width="4"/>'
            body+=f'<path d="M{margin+27} 110H{w-margin-27} M{margin+27} {h-99}H{w-margin-27}" stroke="url(#metal)" stroke-width="9"/>'
        body+=f'<rect x="{mid-w*.34:.1f}" y="76" width="{w*.68:.1f}" height="108" rx="21" fill="url(#paper)" stroke="url(#metal)" stroke-width="6"/>'
        body+=f'<rect x="{mid-w*.34+8:.1f}" y="84" width="{w*.68-16:.1f}" height="92" rx="15" fill="none" stroke="#CBA861" stroke-width="2"/>'
        # Blank text zone at top and bottom, always native labels.
        body+=f'<circle cx="{mid}" cy="{h*.52:.1f}" r="{min(w*.26,h*.18):.1f}" fill="{accent}" fill-opacity=".16" stroke="url(#metal)" stroke-width="8"/>'
        body+=motif(kind,mid,h*.52,min(w/190,2.25),stroke='#FFF2C8',fill=accent)
        body+=f'<rect x="{mid-w*.29:.1f}" y="{h-156}" width="{w*.58:.1f}" height="86" rx="18" fill="url(#paper)" stroke="url(#metal)" stroke-width="6"/>'
        for corner in [(margin+34,margin+35,-27),(w-margin-34,margin+35,115),(margin+34,h-margin-35,-110),(w-margin-34,h-margin-35,35)]:body+=leaf(*corner, size=.8 if variant=='pocket' else 1)
        body+=f'<path d="M{mid-26} 37L{mid} 21L{mid+26} 37L{mid} 54Z" fill="url(#metal)"/>'
        svgtext=wrapsvg(w,h,body,primary)
        svgpath=SV/'covers'/f'gm_ws21_cover_{theme}_{variant}.svg';svgpath.parent.mkdir(parents=True,exist_ok=True);svgpath.write_text(svgtext)
        pngpath=SP/'covers'/f'gm_ws21_cover_{theme}_{variant}.png';render(svgtext,pngpath,width=480 if variant!='pocket' else 360)
        record(f'gm_ws21_cover_{theme}_{variant}','word_search','book_covers',pngpath,svgpath.relative_to(ROOT).as_posix(),
            {'theme':theme,'variant':variant,'editable_svg_sha256':sha(svgpath),
             'label_zones':'blank native title/top and category/bottom','visual_only':True})
    # Theme decorative medallions share frame size while motif varies.
    body='<circle cx="140" cy="140" r="124" fill="url(#metal)" stroke="#7C562B" stroke-width="6"/>'
    body+=f'<circle cx="140" cy="140" r="108" fill="{primary}" stroke="#F7E8B4" stroke-width="5"/>'
    body+='<circle cx="140" cy="140" r="95" fill="url(#paper)" stroke="#B58E4F" stroke-width="4"/>'
    body+=motif(kind,140,136,1.22,stroke=primary,fill=accent)
    body+=leaf(72,205,-58,0.8)+leaf(208,205,58,0.8)
    svgtext=wrapsvg(280,280,body,primary)
    s=SV/'medallions'/f'gm_theme_{theme}_emblem_round.svg';s.parent.mkdir(parents=True,exist_ok=True);s.write_text(svgtext)
    p=SP/'medallions'/f'gm_theme_{theme}_emblem_round.png';render(svgtext,p,280)
    record(f'gm_theme_{theme}_emblem_round','theme','medallions',p,s.relative_to(ROOT).as_posix(),
        {'theme':theme,'editable_svg_sha256':sha(s),'visual_only':True})

# State variants are generated from eight distinct template families.
UI_TYPES=['header_plaque','menu_card','tab','button','portrait_frame','progress_track','achievement_slot','pagination_button']
for family in UI_TYPES:
    for tone in ['forest','cream','gold']:
        base={'forest':'#224C38','cream':'#ECE0C4','gold':'#987033'}[tone]
        if family in ('header_plaque','menu_card','tab','button','progress_track','pagination_button'):
            w,h=(650,180) if family in ('header_plaque','menu_card') else (460,124)
            corner={'button':54,'tab':19,'pagination_button':50}.get(family,27)
            x=13;y=10
            rectfill='url(#cloth)' if tone=='forest' else 'url(#paper)' if tone=='cream' else 'url(#metal)'
            body=f'<rect x="9" y="7" width="{w-18}" height="{h-14}" rx="{corner}" fill="url(#metal)" stroke="#775122" stroke-width="5"/>'
            body+=f'<rect x="{x+7}" y="{y+5}" width="{w-2*(x+7)}" height="{h-2*(y+5)}" rx="{corner-4}" fill="{rectfill}" stroke="#F4E3B7" stroke-width="3"/>'
            body+=f'<rect x="{x+17}" y="{y+15}" width="{w-2*(x+17)}" height="{h-2*(y+15)}" rx="{max(corner-10,10)}" fill="none" stroke="{base if tone != "forest" else "#D6BD77"}" stroke-width="2"/>'
            if family=='progress_track':
                body+=f'<rect x="30" y="40" width="{w-60}" height="{h-80}" rx="14" fill="#F7E9C8" stroke="#A77D36" stroke-width="3"/>'
            if family=='pagination_button':
                body+=f'<path d="M{w/2-24} {h/2} H{w/2+24}" stroke="#B48636" stroke-width="5"/>'
            if family in ('header_plaque','menu_card'):
                body+=leaf(45,h/2,10,.9)+leaf(w-45,h/2,170,.9)
        else:
            w=h=320
            rectfill='url(#cloth)' if tone=='forest' else 'url(#paper)' if tone=='cream' else 'url(#metal)'
            if family=='portrait_frame':
                body=f'<circle cx="160" cy="160" r="149" fill="url(#metal)" stroke="#875A25" stroke-width="6"/>'
                body+=f'<circle cx="160" cy="160" r="127" fill="{rectfill}" stroke="#F2E7C3" stroke-width="5"/>'
                body+='<circle cx="160" cy="160" r="112" fill="none" stroke="#B38A44" stroke-width="3"/>'
                body+=leaf(95,257,-42,1.0)+leaf(225,257,42,1.0)
            else:
                body=f'<path d="M160 11L278 51L268 216Q242 277 160 309Q78 277 52 216L42 51Z" fill="url(#metal)" stroke="#7C5127" stroke-width="6"/>'
                body+=f'<path d="M160 27L260 63L252 207Q228 259 160 285Q92 259 68 207L60 63Z" fill="{rectfill}" stroke="#F5E7BE" stroke-width="5"/>'
                body+=leaf(106,229,-35,.8)+leaf(214,229,35,.8)
        svgtext=wrapsvg(w,h,body,base)
        s=SV/'core_templates'/f'gm_core_ws21_{family}_{tone}.svg';s.parent.mkdir(parents=True,exist_ok=True);s.write_text(svgtext)
        p=SP/'core_templates'/f'gm_core_ws21_{family}_{tone}.png';render(svgtext,p,width=420 if w>400 else 320)
        record(f'gm_core_ws21_{family}_{tone}','core','ui_templates',p,s.relative_to(ROOT).as_posix(),
            {'template':family,'tone':tone,'editable_svg_sha256':sha(s),'blank_native_labels':True})

# Accessible at-a-glance contact sheets with labels outside source sprites.
def contact(items,path,cols=6,cell=(205,250),title='',bg=(248,243,232)):
    cw,ch=cell; rows=(len(items)+cols-1)//cols
    canvas=Image.new('RGB',(cols*cw+24,rows*ch+92),bg)
    d=ImageDraw.Draw(canvas)
    d.text((20,17),title,fill=(36,70,50),font=font(25))
    for i,(name,p) in enumerate(items):
        row,col=divmod(i,cols);x=12+col*cw;y=65+row*ch
        d.rounded_rectangle((x+5,y+5,x+cw-8,y+ch-28),radius=12,fill=(255,252,246),outline=(201,177,132),width=2)
        im=Image.open(p).convert('RGBA');im.thumbnail((cw-30,ch-75),Image.Resampling.LANCZOS)
        canvas.paste(im,(x+(cw-im.width)//2,y+12+(ch-75-im.height)//2),im)
        label=name.replace('gm_core_ws21_','').replace('gm_ws21_cover_','').replace('gm_theme_','')
        if len(label)>27: label=label[:26]+'…'
        d.text((x+9,y+ch-21),label,fill=(44,69,50),font=font(13))
    canvas.save(path,quality=87)

for fam,cols,cell,title in [
('illustrated_rewards',4,(280,292),'Illustrated seasonal & reward sprites | source candidates'),
('book_covers',8,(144,205),'Book covers | 24 themes × 3 editable variants'),
('medallions',6,(190,208),'Theme medallions | 24 reusable emblems'),
('ui_templates',6,(196,194),'Core UI | 8 component families × 3 tone variants')]:
    items=[(x['id'],ROOT/x['path']) for x in manifest['sprites'] if x['family']==fam]
    contact(items,PRE/f'{fam}_contact.jpg',cols,cell,title)

n=len(manifest['sprites'])
manifest['counts']={'illustrated_rewards':16,'theme_book_covers':72,'theme_medallions':24,'core_ui_templates':24,'total':136,'editable_svgs':120,'reference_only_boards':2}
assert n==136, n
pman=ROOT/'manifest.json';pman.write_text(json.dumps(manifest,indent=2)+'\n')
readme='''# GridMates WS21 — Expanded reusable artwork bank\n\n136 candidate assets: 16 hand-painted reward/seasonal illustrations (isolated from an RGB source sheet using neutral-checkerboard cleanup), 72 blank editable theme book covers, 24 reusable medallions, and 24 shared UI template variants.\n\n## Important\n\n- 120 editable SVG source files and their transparent PNG previews included.\n- Two broad concept boards are reference-only and have baked text; do not put them in the app.\n- All titles, labels, counters, achievement states and dynamic text must be Android-native.\n- This is an **asset reserve, not a game build**. No APK, save data, room protocol or unlock state changes.\n- The illustrated atlas was RGB with white/checkerboard baked in. Cutouts are technically transparent after color-key cleanup, but must be visually reviewed for halos before shipping.\n- Bundle source and SHA-256 manifest for exact regeneration and provenance.\n- To regenerate covers/medallions/UI source and PNGs use build_gridmates_ws21.py plus source assets, Pillow, NumPy, OpenCV, CairoSVG.\n- Only ship approved optimized sprites, not every archived asset in future Android releases.\n'''
(ROOT/'README.md').write_text(readme)
# Copy builder into pack (ready to regenerate once unpacked at /mnt/data; path may be changed via BASE).
(ROOT/'tools').mkdir(exist_ok=True)
if HERE.resolve()!=(ROOT/'tools'/'build_gridmates_ws21.py').resolve():
    shutil.copy2(HERE,ROOT/'tools'/'build_gridmates_ws21.py')
ZIP=BASE/'GridMates_Asset_Expansion_WS21.zip'
with zipfile.ZipFile(ZIP,'w',zipfile.ZIP_DEFLATED,compresslevel=7) as z:
    for p in sorted(ROOT.rglob('*')):
        if p.is_file():
            info=zipfile.ZipInfo(p.relative_to(ROOT).as_posix(),date_time=(2026,1,1,0,0,0))
            info.compress_type=zipfile.ZIP_DEFLATED
            info.external_attr=(0o644 << 16)
            z.writestr(info,p.read_bytes(),compress_type=zipfile.ZIP_DEFLATED,compresslevel=7)
with zipfile.ZipFile(ZIP,'r') as z:
    assert z.testzip() is None
    mm=json.loads(z.read('manifest.json'))
    assert len(mm['sprites'])==136
    for obj in mm['sprites']:
        assert hashlib.sha256(z.read(obj['path'])).hexdigest()==obj['sha256']
        if obj['source'] and obj['source'].endswith('.svg'):
            assert hashlib.sha256(z.read(obj['source'])).hexdigest()==obj['details']['editable_svg_sha256']
print(json.dumps({'pack':str(ZIP),'zip_bytes':ZIP.stat().st_size,'zip_sha256':sha(ZIP),'counts':manifest['counts'], 'all_source_hashes_passed':True},indent=2))