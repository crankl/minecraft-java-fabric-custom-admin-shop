# Crankl Admin Shop

Crankl Admin Shop is a server-side Fabric mod for Minecraft Java 26.3. Players use ordinary vanilla inventory screens; no client mod is required.

## Compatibility

- Minecraft Java `26.3`
- Java `25`
- Fabric Loader `0.20.1` or newer
- Fabric API `0.166.0+26.3`

The project uses the current official Fabric 26.3 toolchain: Loom `1.19-SNAPSHOT` and Gradle 9.6. These versions were checked against the [Fabric 26.3 announcement](https://www.fabricmc.net/2026/09/15/263.html) and [Fabric Develop](https://fabricmc.net/develop/).

## Build

Install JDK 25, then run:

```sh
./gradlew build
```

The remapped server mod JAR is written to `build/libs/`. Do not install the `-sources` JAR.

## Installation

1. Back up the server world and stop the server.
2. Install Fabric Loader for Minecraft 26.3 and place the matching Fabric API JAR in the server `mods` directory.
3. Copy `crankl-admin-shop-0.1.0.jar` into the same `mods` directory.
4. Start the server once. It creates `config/crankl-admin-shop.json`.
5. Stop the server before making a large configuration change, edit the JSON, then start it; or use `/shop admin reload` for a valid live reload.

This mod is marked `environment: server`. Clients only need a normal Minecraft 26.3 installation using the Fabric server's required protocol.

## Commands

| Command | Access | Purpose |
| --- | --- | --- |
| `/shop` | Everyone | Open the `main` profile. |
| `/shop <profile>` | Everyone | Open a named profile. |
| `/shop admin bind <profile>` | Permission level 2 | Bind the looked-at vanilla villager workstation within 6 blocks. |
| `/shop admin remove <x> <y> <z>` | Permission level 2 | Remove a binding in the current dimension. |
| `/shop admin reload` | Permission level 2 | Reload `crankl-admin-shop.json`. |

## Configuration

Copy or adapt [config-example/crankl-admin-shop.json](config-example/crankl-admin-shop.json). Each offer has an output (`item`, `itemCount`) and a price (`priceItem`, `priceCount`). Buy mode exchanges price for output; sell mode exchanges output for price. Item identifiers must be valid Minecraft registry identifiers and counts must be positive integers.

The supplied `main` profile contains the requested sample offers:

- 64 iron ingots for 1 diamond
- 1 diamond for 64 stone
- 16 apples for 1 diamond

Profiles can contain any number of categories. A category page displays up to 36 offers and has previous/next navigation. The quantity menu provides -10, -1, +1, +10, maximum, and confirmation controls.

## Workstations

Only the vanilla `minecraft:acquirable_job_site` workstation tag is accepted. In vanilla 26.3 this is the 13 villager workstations: blast furnace, smoker, cartography table, brewing stand, composter, barrel, fletching table, cauldron, lectern, loom, stonecutter, grindstone, and smithing table.

A binding is saved in `config/crankl-admin-shop-workstations.json` with its dimension and block position, so it persists across restarts. A registered workstation opens its profile only when a player uses it. Villagers never invoke the player interaction callback, so job assignment, POI claims, AI, and trade restocking are untouched. Unregistered workstations are passed through to vanilla. Breaking a tagged bound workstation removes its binding before the block is broken.

## Transaction safety

Shop display inventories reject every extraction and insertion attempt; they are not item containers. The server recalculates payment and destination capacity immediately before every confirmation. It accepts payment across multiple inventory stacks, requires all payment and all output capacity before changing a stack, then performs the exchange server-side. No partial payment is taken and output is never intentionally dropped.

## Automated tests

`./gradlew test` runs pure inventory arithmetic tests that cover multi-stack payment, complete output-capacity checks, and mismatched payment items. `./gradlew build` runs the same tests and produces the remapped JAR.

## First integration-test checklist

- [ ] Join with an unmodded 26.3 client and open `/shop`.
- [ ] Verify category, buy/sell toggle, 36-offer paging, and all quantity controls.
- [ ] Buy and sell each sample offer using payment split across several inventory slots.
- [ ] Attempt a purchase with missing payment and with insufficient output space; verify nothing changes.
- [ ] Try left-click, shift-click, drag, hotbar swap, double-click, and drop actions against every GUI placeholder; verify no item can be taken.
- [ ] Bind each of the 13 workstation types, restart the server, and verify its profile opens.
- [ ] Verify an unbound workstation retains its normal vanilla UI.
- [ ] Verify villagers can claim, use, and restock at a bound workstation.
- [ ] Break a bound workstation, replace it, restart the server, and verify the old position is no longer bound.
- [ ] Verify a non-operator cannot execute any `/shop admin` command.
