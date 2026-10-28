# Changelog

## 0.19.2 - Funko box fixes

Fixed
- Texture glitches seen from different angles: Funko blocks go back to the cutout render layer (the translucent layer sorted faces wrongly), the glass is now an open window with opaque glint lines, and every tile in the texture has an 8 px edge padding so neighbouring tiles no longer bleed into each other at a distance.
- The side window is now on the right side of the box when looking at its front (it was on the left).
- The front-right corner is open: the front and side windows run to the corner edge with no blue post between them, and the thin edge faces there are transparent.

Changed
- The box is smaller and more compact: 12 wide, 14 tall, 8 deep (was 13 x 14.5 x 9). `BW`, `BH`, `BD` at the top of `make_funkos.py` change the size, and the figure scale follows.
- Hitbox updated to match.

## 0.19.1 - Squarer Funko boxes in pixel style

Changed
- Funko boxes are now nearly square (13 x 14.5 x 9) instead of tall and thick, and drawn in pixel art at 16 px per block with a Minecraft-style bitmap font: coloured header with emblem and name, white frame, glass window with glints, bottom band with the skin's face and the numbered badge, striped sides and a back with the name and number.
- The right side of the box has a window too, so the figure can be seen from the side.
- The figure no longer has a base plate; it stands on the floor of the box, lower and larger (scale 0.64).
- The window is real glass: Funko blocks now use the translucent render layer.
- Hitbox follows the new box size.
- `make_funkos.py` rebuilt for the new design; textures are bigger because of the pixel detail.

## 0.19.0 - Funko box redesign and generator

Changed
- Funko Pop boxes redesigned: solid colour per Funko (red, blue, orange, green, charcoal, yellow, purple, teal) with a white border, the character name on top next to a small emblem, a large window with the figure, the skin's face on the bottom left and a numbered badge (001, 002, ...) on the bottom right.
- Funko blocks are now registered from a list: `ModBlocks.FUNKOS` (`registerFunkos(...)`). The creative tab and the cutout layer loop over that list, so the eight `FUNKO_*` constants are gone.

Added
- `tools/funko/`: `funkos.json` (data for each Funko), `skins/` (the eight skins), `make_funkos.py` and `README.txt`. Adding a new Funko is a skin file plus one line in the json, then running the script.

## 0.18.1 - Funko Pops in a box

Changed
- Each Funko Pop now comes in a collector box: magenta cardboard with a "POP!" header, a window showing the figure inside, and the character name on a purple label at the bottom. The figure is scaled down to fit. Models and textures of the eight Funkos are regenerated (the textures hold the box art, so they are bigger).
- `FunkoBlock` hitbox now matches the box and turns with the facing.

## 0.18.0 - Funko Pops

Added
- Eight Funko Pop blocks (`funko_zzilexx`, `funko_renkunshiti`, `funko_tv_toxic_vt_2`, `funko_tv_toxic_vt_1`, `funko_black`, `funko_mushroom_king`, `funko_kael`, `funko_azazel`) in a new "Funko Pops" creative tab. Standing figure with an oversized head, small body and dark base, built from each skin. No squeak on right click. The crucified plush has no Funko.
- `FunkoBlock` class, models, blockstates, textures (`funko_*_skin.png`), loot tables, tag entries and translations ("Funko Pop de ..." / "... Funko Pop").

## 0.17.2 - Plush on a cross

Added
- `plushie_crucified` ("Peluche crucificado" / Crucified Plushie): plush of the supplied skin standing with arms stretched along a wooden cross. Own `CrossPlushBlock` with a wider hitbox that turns with the facing; the wood is drawn inside the plush texture. Temporary name until the owner of the skin is chosen.

## 0.17.1 - Azazel plush

Added
- `plushie_azazel` ("Peluche de Azazel" / Azazel Plushie) in the Plushies tab, same design as the others.

## 0.17.0 - Plushie ids renamed

Changed
- Every plush id now starts with `plushie_`: `plushie_zzilexx`, `plushie_renkunshiti`, `plushie_tv_toxic_vt_2`, `plushie_tv_toxic_vt_1`, `plushie_black`, `plushie_mushroom_king`, `plushie_kael`. Blocks, items, models, blockstates, textures (`plushie_*_skin.png`), loot tables, tag entries, translation keys and Java constants (`PLUSHIE_*`) follow the new names. Displayed names are unchanged.
- Plushes placed in existing worlds or kept in inventories under the old ids are lost.

Removed
- All files named `plush_*`, `plush.json` and `plush_skin.png` in blockstates, models, textures and loot tables (replaced by the `plushie_*` ones).

## 0.16.6 - Kael plush name

Changed
- The purple plush is now "Peluche de Kael" (Kael Plushie).

## 0.16.5 - Purple plush

Added
- "Peluche morado" (Purple Plushie) in the Plushies tab, built from the new skin (slim arms). Temporary name until the owner of the skin is chosen.

## 0.16.4 - Plush sound from the supplied audio

Changed
- The plush sound now comes from the supplied `plush_squeak1.ogg`: the 9.6 s clip was split at its silences into nine mono clips (`chillido1`-`chillido9`, 0.35-1.05 s) and `sounds.json` plays one at random per click. The earlier generated sounds stay in the folder, unused.

## 0.16.3 - Plush bottom faces

Fixed
- The underside of every plush (head, body, arms, legs) was missing and could be seen through. All parts now have a bottom face. Models and textures of the six plushes are regenerated.

## 0.16.2 - Mushroom King plush

Added
- "Peluche del Rey Hongo" (Mushroom King Plushie) in the Plushies tab, same design and hand display as the others. Built from the 128x128 skin, so it keeps its detail.

## 0.16.1 - Plush held in hand

Changed
- Held plushes keep the placed size and the seated pose: third person at full size, first person at 0.8, both upright with the front visible (first person faces you). Left hand added.

## 0.16.0 - Three more plushes

Added
- Three plushes in the Plushies tab, same design and size as the others: "Peluche de Tv_toxic_vt 2" (built from the 512x512 skin, so it keeps its high detail), "Peluche de Tv_toxic_vt 1" and "Peluche de Black".
- Models, blockstates, textures (`plush_tvtoxic2_skin.png` is 512x384, the others 64x48), loot tables, cutout layer and translations.

## 0.15.4 - Soft plush squish sounds

Added
- Four soft fabric-and-stuffing squish sounds in `sounds/plush/`: `aplastar`, `felpa`, `mullido`, `apretar` (muffled noise with a low thump, no chirp).

Changed
- `sounds.json` now plays those four at random. The old chirp sounds stay in the folder, unused.

## 0.15.3 - Plush names and sound folder

Changed
- Names: English "Zzilexx Plushie" / "Renkunshiti Plushie"; Spanish "Peluche de Zzilexx" / "Peluche de Renkunshiti".
- Squeak sounds now live in `assets/modcasino/sounds/plush/` (classic, double, duck, grave, pop, largo). `sounds.json` picks which ones play; it uses `classic` by default. Own `.ogg` files (mono) can be dropped in that folder and listed there.

Removed
- `sounds/plush_squeak1.ogg`, `plush_squeak2.ogg`, `plush_squeak3.ogg` (replaced by the folder; delete them from the project).

## 0.15.2 - Plush names and squeak sound

Changed
- The plushes are now called "Zzilexx Pushie" (mushroom hat) and "Renkunshiti Pushie" (navy).
- Right click plays a custom squeaky-toy sound with three variants and slight random pitch, instead of the vanilla slime sound.

Added
- `ModSounds`, `sounds.json`, three `plush_squeakN.ogg` files (generated, original) and the subtitle.

## 0.15.1 - Second plush

Added
- `Navy Plush` block: same seated plush design built from the second skin (slim arms), in the Plushies tab.

## 0.15.0 - Plush of the skin

Added
- `Plush` block: a seated chibi plush built from the supplied skin (big cubic head with the mushroom hat, small body, arms out, legs forward). It faces the player when placed.
- Right click: squeaky squish sound with random pitch and a heart particle.
- Own creative tab "Peluches" (Plushies) inside the casino mod, separate from the main casino tab. Future plushes go there.
- Loot table, translations (Plush / Peluche).
- Texture `textures/block/plush_skin.png` is generated from the skin with every face pre-oriented; the block uses the cutout layer for the hat and jacket overlays.

## 0.14.3 - Flat colour lever shaft

Changed
- The lever shaft, hub and base block use one flat silver-grey colour, with no highlight bands or lines. The texture is now a plain 16x16 image.

## 0.14.2 - Lever back to a blocky look

Changed
- The lever is made of boxes again, in the Minecraft style, but with a lever shape: a square mount plate on the side, a small hub, a base block, a thin square shaft, and a knob built from three crossed boxes so it reads as a rounded ball without curves.
- Shaft, hub and mount are polished metal; the knob is black.
- Removed the round mount, the rings and the sphere (the code for cylinders and spheres is gone). The old collar under the knob and the stray pixel stay removed.

Removed
- `textures/entity/slot_lever_ball.png` (unused now; delete it from the project).

## 0.14.1 - Lever: round mount, metal shaft, clean knob

Changed
- The grey square plate behind the lever is gone from the model; the renderer now draws a round mount (a 32-sided disc with two smaller steps and a hub) in polished metal.
- The shaft, rings and hub are bright metal instead of black. Only the knob stays black.
- Removed the flared collar and the ring under the knob; the shaft now goes straight into the ball.
- The black hub no longer overlaps the plate, which was leaving a stray pixel: the hub ends exactly where the mount begins.

## 0.14.0 - Slot machine: round, textured lever

Changed
- The lever is no longer made of boxes. It has a round hub with a collar, a slightly tapered 14-sided shaft with two rings and a flared neck, and a smooth sphere as the knob.
- New black metal texture with a soft highlight band that makes the shaft look cylindrical, and a glossy black knob texture with a specular spot. It stays black as before.
- It moves exactly as before; only the shape and texture changed.

## 0.13.8 - Roulette table no longer drops in creative

Fixed
- Breaking the table in creative mode dropped the item as if it were survival. Removing the other parts triggered a chain of shape updates that destroyed the origin block with drops enabled (that chain ignores the "no drops" flag). The other parts are now removed without neighbour shape updates, so nothing drops in creative and survival still drops exactly one table.

## 0.13.7 - Mirrored 777 and stray yellow pixel

Fixed
- The 777 on the reels of the slot machine item were mirrored. The reel faces in the item model had their horizontal coordinates reversed; they now read correctly.
- A loose yellow patch showed on the roulette base. The ledge of the plinth sampled a random part of its texture, which includes the brass edge. Its top and bottom faces now read a plain dark area.

## 0.13.6 - Item models: texture coordinates out of range

Fixed
- Dropped and held items showed wrong textures (a flat yellow block on the slot machine, stripes on the roulette sides). The inventory models extend past one block (0 to 32 high for the machine, up to 48 wide for the table), and faces without explicit coordinates took texture coordinates outside 0-16, so the game read pixels from neighbouring textures. Every such face now has explicit coordinates in range.
- The slot machine item now shows the neon side design on the upper half too. The chair backrest, which pokes slightly above one block, got the same fix.

## 0.13.5 - Roulette table: cabinet textures

Changed
- The base had strong horizontal bands that read as stacked layers. The body is now dark walnut with a fine vertical grain and a gold inlay frame, the plinth is near-black with a brass edge, and the apron under the red rail is black leather with a gold piping line, so the table looks like one cabinet.
- The red padded rail keeps its rounded look but now sits on the black apron instead of continuing in red down the whole side.

Cleanup (delete from the project if present)
- `assets/modcasino/models/block/roulette.json` (old 2x2 model, unused)
- `assets/modcasino/textures/block/roulette_felt.png` (old felt, unused)

## 0.13.4 - Missing textures in item models, chips in third person

Fixed
- The slot machine item showed the purple and black "missing texture" pattern on the reels: item models only see textures under `textures/block`, and the reels lived under `textures/entity`. A copy now sits in `textures/block/slot_machine_reels.png`.
- The swivel chair item showed the same pattern on the seat, backrest and armrests: those parts still pointed at a texture that no longer existed. They now use the new black leather textures.
- The roulette table item had the same problem on the wheel; it now uses `textures/block/roulette_wheel_item.png`.
- Chips held in third person were upside down. The third-person display is turned 180 degrees about the chip's axis; the left hand now has its own display too.

## 0.13.3 - Roulette ball is now a sphere

Changed
- The ball is a real sphere (24 segments by 12 rings) instead of a cube, a little larger, and it rolls while it circles the wheel and rests on the wheel when it stops.
- New 128x64 ivory texture with fine grain, four meridian seams, an equator seam and a few small dimples.

## 0.13.2 - Roulette: clean rail, plain red and black cells

Changed
- The rail was corrugated on the curves and showed stacked bands on the edge. The curve now uses finer steps (a quarter of a pixel), the rail top is a flat padded red, and one wall texture runs continuously over the raised rail and the apron below it, so the edge reads as a single padded surface.
- The red and black cells at the bottom no longer have a diamond; they are plain colour with the gold outline.

## 0.13.1 - Roulette: oval shape, clean gold ring, chip stacks

Changed
- The table outline is rounded: wide curved back corners around the wheel and small rounded front corners. The plinth, body, top, red rail and felt all follow the curve, and the gold border on the felt follows it too. The collision box stays rectangular.
- The gold ring around the wheel no longer touches the red rail. The wheel, ring and ball are about 10% smaller and moved forward so everything fits inside the rail. The ball track and pockets were adjusted to the new wheel.
- Cup holders moved inward so the curve does not cut them.
- Chip stacks: chips are slightly smaller, sit directly on the felt, and are spaced exactly by their thickness. A stack holds 24 chips; after that the cell shows two stacks side by side, and up to four (2x2) with no overlap. A single stack stays centred in its cell.
- The inventory model follows the new outline.

## 0.13.0 - Roulette: 3x2 table with the full layout

Changed
- The table is now 3 blocks wide and 2 deep. Placing it needs a free 3x2 area; breaking any part removes the whole table and refunds the bets.
- The felt shows the full European layout: 0, the 36 numbers in red and black, the three "2:1" columns, the three dozens, and 1-18, even, red, black, odd, 19-36. The wheel sits in the middle of the back row with two cup holders on each side.
- You can now bet on any of these cells. Payouts per chip: a number returns 36 (35:1), a column or a dozen returns 3 (2:1), and 1-18, 19-36, even, odd, red and black return 2 (1:1). Zero is now a normal number and also returns 36; before it returned 38.
- Chips are smaller and stack inside the cell that was clicked.
- Table design: padded red leather rail around the edge with a raised armrest, a dark plinth, a body set back under the top, and the base no longer has a gap between blocks.
- The felt is split into six 256x256 textures, one per block.

Removed
- `textures/block/roulette_felt.png` (replaced by the six `roulette_felt_*` files).
- Bets saved in the old table are not compatible with the new cells; they are only kept during a round, so this matters only for a table saved mid-round.

## 0.12.1 - Roulette: round frame, no title text

Changed
- Removed the RULETA and CASINO lettering from the felt.
- The square gold frame around the wheel is now a round polished gold ring drawn by the renderer (48 segments), so it follows the wheel exactly. The inventory model no longer shows a frame, only the wheel.

## 0.12.0 - Roulette table: higher quality look

Changed
- Wheel texture redrawn at 512x512: wooden bowl with gold ring, shaded pockets with gold dividers, shaded numbers, brass turret with spokes and a pearl centre.
- Felt texture at 512x512 with woven fabric, vignette, gold border with beads, and bet zones with depth, ornaments and shadowed lettering. The zones keep exactly the same positions, so clicking works as before.
- The RULETA and CASINO titles are smaller and moved to the corners, so the wheel frame no longer covers them.
- The table edge is now a padded black leather rail with a gold line.
- The base has a dark plinth and a plank body slightly set back under the top, with 64x64 wood grain.
- The wheel frame has its own polished gold texture and the ball is a pearl-white texture; the roulette no longer shares textures with the slot machine.

## 0.11.1 - Swivel chair: realistic black leather

Changed
- Removed the square frame, dashed stitching and vertical lines from the seat, backrest and armrests.
- Black leather with fine pebbled grain and soft shading: lighter in the centre and highlight, darker at the edges, so the cushions look padded. The backrest has one subtle centre seam.
- Base and wheels in dark brushed metal with a soft vertical highlight instead of hard stripes.

## 0.11.0 - Swivel chair: all black

Changed
- The chair is now plain black, with no neon or coloured accents. New 64x64 textures; the chair no longer shares textures with the slot machine.

## 0.10.1 - Display text in Spanish

Changed
- The machine display now reads INSERTAR FICHA.

## 0.10.0 - Slot machine: dark neon look

Changed
- New 64x64 dark gunmetal textures for the body, front, trims and tray (the old ones were 16x16).
- Sides have a dark panel design with cyan and magenta neon lines, lit at full brightness and pulsing.
- Buttons and lever (shaft, knob and ball) are now fully black.
- Neon tubes frame the reel window; the marquee has a neon JACKPOT 777 sign with chasing lights that speed up while spinning and turn rainbow on a win.
- Reel symbols redrawn at 64x64.
- Removed the unused old machine textures and the old bulb texture.

## 0.9.6 - Slot machine top flicker fixed

Fixed
- The top of the slot machine flickered between black and gold. Three faces sat on the same plane at the top (the marquee, the body and the gold cap), and the game could not choose which to draw. The two lower ones no longer draw their top face, so only the gold cap shows. Same fix in the inventory model.
- Checked every model in the mod for faces on the same plane: no other overlaps remain.

## 0.9.5 - Chip number mirrored in item frames

Fixed
- In an item frame the chip showed the underside, whose texture was flipped vertically, so the number looked mirrored. The bottom face now uses the same orientation as the top, so the number reads correctly in frames and on the roulette felt.

## 0.9.4 - Chip face without band, upright in item frames

Changed
- The chip face no longer has the rectangular band and the two lines behind the value; the number sits directly on the centre disc, inside the ring of dashes.
- In an item frame the chip was shown rotated 180 degrees. The chip model's `fixed` display now turns it the right way up. The roulette renderer adds the same half turn so the chips on the felt look as before.

## 0.9.3 - Chips stack on the roulette

Changed
- Bets on the felt no longer overlap. Chips are smaller and every chip placed in a box goes on top of the pile in that box, in the order they were bet.
- A pile holds 12 chips. The 13th starts a new pile next to it, up to six piles per box; chips beyond 72 per box are still bet but not drawn.
- Each chip is turned a few degrees so the piles do not look perfectly straight.

## 0.9.2 - Chip thick edge, spikes removed

Fixed
- The chip edge showed green spikes around the disc. The body was built from overlapping rotated squares, whose corners stick out; it is now a ring of 16 flat plates that follow the circle.

Changed
- Thicker edge: the chip is 3.2 pixels thick with a lip at the top and bottom, and coloured spots on the wall between them. The spots use the two colours of each chip.
- Smaller and better placed in the first-person and third-person hand views.

## 0.9.1 - Chip face design

Changed
- The chips now use the casino chip design: ten alternating coloured segments around the rim with dark dividers, a centre disc with a ring of dashes and a band with the value between two lines. Replaces the spiked disc of 0.9.0.
- Each chip has its own face texture, `textures/item/chip_<value>.png` (256x256), drawn on both faces of a thin 3D disc with a coloured edge. Values shown: 1.000, 5.000, 25.000, 100.000, 500.000, 1M, 5M, 10M, 50M, 100M, 500M and 1B.
- Rim colours follow the chip table. When the second colour is black the centre is white with black text, as in the reference; with a white or gold second colour the centre takes the chip's main colour and the text uses the second colour.
- In the inventory the chip is tilted to show its face and its edge.

## 0.9.0 - 3D casino chips

Changed
- The 12 chips are now small 3D models shaped like casino chips instead of a flat icon: a 16-sided disc with an edge ring, an inner ring, a centre mark and eight spots on the rim, drawn the same on both faces. They lie flat on the ground when dropped and on the roulette felt.
- Colours follow the table of chips: 1K green and black, 5K red and black, 25K black and white, 100K green and white, 500K red and white, 1M yellow and white, 5M purple and white, 10M orange and white, 50M blue and white, 100M brown and white, 500M black and gold, 1B gold.
- All the chips share one small colour texture, `textures/item/chip_palette.png`. The large `1k_chip.png` is no longer used and was removed.

## 0.8.0 - Roulette table 2x2 for several players

Changed
- The roulette is now a table of 2x2 blocks and 1 block high. Placing the item fills the four blocks in front of and to the right of the clicked block, relative to the player; it needs all four free. Breaking any part removes the whole table and drops one item.
- The block model `models/block/roulette.json` is replaced by one model per part (`roulette_front_left`, `roulette_front_right`, `roulette_back_left`, `roulette_back_right`) and `roulette_item` for the inventory icon. The old file can be deleted.
- Larger felt (256x256) with big RED, 0 and BLACK boxes showing their odds, and a bigger wheel in the back half of the table.

Added
- Several players can bet in the same round. The first bet opens a 15 second betting window; any player can add chips to any box with any chip from the `modcasino:chips` tag, as many times as they like (one chip per click). Players within 10 blocks see a countdown. After that the wheel spins and "No more bets" is shown.
- Each bet is shown as a chip lying on its box on the felt, using the chip's own icon.
- Winnings go straight to each winner's inventory in chips of the type they bet (dropped at their feet if the inventory is full, or on the table if they are offline). Each bettor sees the result and whether they received chips; players nearby who did not bet see the result.
- Breaking the table while bets are open or the wheel is spinning returns all the chips to the front of the table.
- Limit of 256 bets per round.

Payouts are unchanged: red or black returns 2, a green 0 returns 38.

## 0.7.0 - Roulette table

Added
- Roulette Table block (`modcasino:roulette`), in the Modcasino creative tab. Single block, faces the player when placed.
- Bet by right-clicking one of the three zones painted on the near edge of the felt with any chip from the `modcasino:chips` tag: RED (left), 0 (centre), BLACK (right). One chip is the bet; a click anywhere else shows how to bet.
- The wheel turns and slows down while a ball orbits the opposite way, then drops into the winning pocket. Wheel and ball are drawn by a block entity renderer; the wheel keeps the angle it stopped at.
- European single-zero wheel with the real pocket order. Each spin lasts 10 seconds with ball clicks that space out as it slows.
- Payouts are paid in chips of the type bet and drop in front of the table. Red or black returns 2, a green 0 returns 38, giving 97.3% expected return on colours and 102.7% on 0.
- Players within 20 blocks see the number, its colour and whether the table paid.

## 0.6.3 - Consistent spin direction and empty chair spin

Fixed
- Clicking twice spun the chair one way and then the other, because the direction depended on the clicked side, which changes as the chair turns. While the chair is spinning every click now adds speed in the same direction; the side only decides the direction when the chair starts from rest.

Added
- An empty chair can be spun by crouching and right-clicking it with empty hands. It turns by itself, slows down and stops, and keeps its final angle. Sitting on a spinning empty chair keeps its momentum.

## 0.6.2 - Camera and body follow the spinning chair

Fixed
- Spinning the chair left the player's camera and character still, and the screen went black. The server-side rotation packet was removed.
- The chair now exposes a synced "spinning" flag. While it is set, each client applies the seat's per-tick rotation to its own player (view, body and head), so the camera and the character turn with the chair.

## 0.6.1 - Push an occupied chair

Changed
- Another player can right-click an occupied chair to spin it, with the same push and direction rules as the rider. Previously they only saw "the chair is occupied".

## 0.6.0 - Spinning chair

Added
- Right-clicking the chair while seated spins it. Each click adds speed (up to a limit), the chair keeps turning and slows down with friction until it stops. The player turns with it, camera included.
- The side of the seat that is clicked decides the direction: clicking the right half spins it left, the left half spins it right.
- A soft sound plays on each push.

Changed
- The chair only follows where the player looks once it has stopped spinning.

## 0.5.0 - Swivel chair

Added
- Swivel Chair block (`modcasino:swivel_chair`), in the Modcasino creative tab. Right-click to sit; shift to stand up.
- While seated, the chair turns smoothly to follow where the player looks (up to 14 degrees per tick). The base stays still and the seat, backrest and armrests rotate, rendered by a block entity renderer.
- The seat is an invisible entity (`modcasino:chair_seat`) that is never saved and removes itself when the rider leaves or the chair is broken. The chair remembers its last angle.
- A placed chair faces the player. Occupied chairs show a message.

## 0.4.1 - One cherry pays

Changed
- A single cherry now pays 1 chip, so the bet is returned.
- To keep the game hard, the rest of the table was lowered: 777 pays 100, three diamonds 40, three BAR 12, three bells 8, three lemons 5, three cherries 5, two cherries 2.
- Expected return is 103.3% (1785 of 1728). A prize lands on about 60.4% of spins, mostly 1-chip refunds; the jackpot stays 1 in 1728.

## 0.4.0 - Player-favoured payouts

Changed
- Payout table rebalanced: 777 pays 100, three diamonds 50, three BAR 25, three bells 18, three lemons 12, three cherries 10, exactly two cherries 3.
- Expected return is now 105.2% (1817 of 1728 chips per 1728 spins), up from 72.9%. A prize lands on about 18.2% of spins and the jackpot on 1 in 1728, so most sessions lose chips and some win big.
- Rationale: the machine is the only source of chips, so it must be able to grow a player's stack.

## 0.3.1 - Double drop fix

Fixed
- Breaking the upper half of the machine in survival dropped two machines. The lower half was dropped by hand and then dropped again when it was removed by the shape update. The lower half is now removed without drops (flag 35) after the manual drop, as vanilla double blocks do.

## 0.3.0 - Casino chips

Added
- The 12 casino chips ported from the chips project: 1K, 5K, 25K, 100K, 500K, 1M, 5M, 10M, 50M, 100M, 500M and 1B, with names and tooltips in Spanish (Chile) and English. Ids are `modcasino:<value>_chip`.
- The chips are in the Modcasino creative tab after the machine.

Changed
- The `modcasino:chips` tag now lists only the 12 chips. Emeralds are no longer accepted.
- All chips share the texture `item/1k_chip.png` until the other textures exist; each model points to it, so replacing a chip's texture only needs editing its model.

## 0.2.0 - Chips and mirrored reels fix

Changed
- The reel symbols were drawn mirrored; they now read correctly.
- The lever moved to the right side of the machine as seen from the front.
- The machine accepts any item in the item tag `modcasino:chips`, not only emeralds. One chip is the bet, whichever the player inserts.
- Winnings are paid in chips of the same type that was bet (a win of 6 with a 5,000 chip pays 6 chips of 5,000). The chip type is saved with the machine, so a spin interrupted by a reload still pays the right chips.
- Messages no longer name a coin: "Insert any money chip to play".

To add the casino chips, list them in `src/main/resources/data/modcasino/tags/item/chips.json` (a datapack can also extend the tag). Emerald stays in the tag for testing until the chips are listed.

Files
- Code: `block/SlotMachineBlock.java`, `block/SlotMachineBlockEntity.java`, `slot/SlotPayouts.java`, `client/render/SlotMachineRenderer.java`
- Assets and data: `models/block/slot_machine_upper.json`, `models/item/slot_machine.json`, `lang/en_us.json`, `es_cl.json`, `tags/item/chips.json`

## 0.1.0 - Jackpot 777 slot machine

Added
- Slot machine block, two blocks tall, with a facing that follows the player. Placing it needs one free block above; breaking either half drops one machine, and creative breaking drops nothing.
- Right-click with a coin (currently an emerald, one per play) to play. The lever is pulled, the three 3D reels spin and stop one after another, and the marquee bulbs chase while the reels turn. Right-click with another item or an empty hand shows a message with the price.
- Payouts in coins that pop out of the tray: 777 pays 100 (jackpot, with fireworks, a sound and a message to nearby players), three diamonds 40, three BAR 20, three bells 12, three lemons 8, three cherries 6, exactly two cherries 2. Each reel is a 12-symbol drum chosen at random, which gives about 73 % return.
- Spin state is saved with the block, so a machine reloaded mid-spin finishes and pays.
- Creative tab "Modcasino" with the machine.
- Spanish (es_cl) and English texts.

Files
- Code: `src/main/java/cl/hakyo/Modcasino.java`, `block/SlotMachineBlock.java`, `block/SlotMachineBlockEntity.java`, `slot/SlotSymbol.java`, `slot/SlotPayouts.java`, `registry/ModBlocks.java`, `registry/ModBlockEntities.java`, `registry/ModCreativeTabs.java`
- Client: `src/client/java/cl/hakyo/client/ModcasinoClient.java`, `client/render/SlotMachineRenderer.java`
- Assets: `blockstates/slot_machine.json`, `models/block/slot_machine_lower.json`, `slot_machine_upper.json`, `models/item/slot_machine.json`, `textures/block/slot_machine_*.png`, `textures/entity/slot_reels.png`, `slot_bulb.png`, `lang/en_us.json`, `es_cl.json`
- Data: `loot_table/blocks/slot_machine.json`, `tags/block/mineable/pickaxe.json`
- Metadata: `src/main/resources/fabric.mod.json`

To change the coin or the prices, edit `COIN`, `BET`, `JACKPOT` and the table in `slot/SlotPayouts.java`.
