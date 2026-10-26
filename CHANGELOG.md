# Changelog

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
