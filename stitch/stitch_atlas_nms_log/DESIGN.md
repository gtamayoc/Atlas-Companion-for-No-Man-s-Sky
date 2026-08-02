---
name: Atlas NMS Explorer Interface
colors:
  surface: '#210e0b'
  surface-dim: '#210e0b'
  surface-bright: '#4c332f'
  surface-container-lowest: '#1b0907'
  surface-container-low: '#2b1613'
  surface-container: '#2f1a17'
  surface-container-high: '#3b2420'
  surface-container-highest: '#472f2b'
  on-surface: '#ffdad4'
  on-surface-variant: '#ebbbb4'
  inverse-surface: '#ffdad4'
  inverse-on-surface: '#422a27'
  outline: '#b18780'
  outline-variant: '#603e39'
  surface-tint: '#ffb4a8'
  primary: '#ffb4a8'
  on-primary: '#690100'
  primary-container: '#ff5540'
  on-primary-container: '#5c0000'
  inverse-primary: '#c00100'
  secondary: '#ffffff'
  on-secondary: '#003737'
  secondary-container: '#00fbfb'
  on-secondary-container: '#007070'
  tertiary: '#f1c100'
  on-tertiary: '#3d2f00'
  tertiary-container: '#d0a600'
  on-tertiary-container: '#4f3d00'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffdad4'
  primary-fixed-dim: '#ffb4a8'
  on-primary-fixed: '#410000'
  on-primary-fixed-variant: '#930100'
  secondary-fixed: '#00fbfb'
  secondary-fixed-dim: '#00dddd'
  on-secondary-fixed: '#002020'
  on-secondary-fixed-variant: '#004f4f'
  tertiary-fixed: '#ffe08b'
  tertiary-fixed-dim: '#f1c100'
  on-tertiary-fixed: '#241a00'
  on-tertiary-fixed-variant: '#584400'
  background: '#210e0b'
  on-background: '#ffdad4'
  surface-variant: '#472f2b'
typography:
  display-lg:
    fontFamily: Space Grotesk
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.2'
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.2'
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  code-sm:
    fontFamily: Space Mono
    fontSize: 12px
    fontWeight: '400'
    lineHeight: '1.4'
    letterSpacing: 0.05em
  label-caps:
    fontFamily: Space Mono
    fontSize: 10px
    fontWeight: '700'
    lineHeight: '1'
    letterSpacing: 0.1em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 4px
  container-margin: 16px
  gutter: 12px
  stack-sm: 8px
  stack-md: 20px
  stack-lg: 40px
---

## Brand & Style
The design system is engineered to evoke the feeling of an advanced ship-board computer or an exosuit HUD. It is a **futuristic, technical** interface tailored for the "No Man's Sky" enthusiast. The aesthetic leans heavily into **Glassmorphism** and **Technical Minimalism**, utilizing deep layering to simulate holographic depth. 

The emotional response should be one of precision, discovery, and immersion. The UI feels like an extension of the game’s lore—utilizing technical grid overlays, scanning lines, and subtle "glow" states to signal data activity. It avoids organic shapes in favor of sharp, engineered geometry and high-contrast status signaling.

## Colors
The palette is rooted in the void of space. The background uses a tiered system of **Deep Space Navy** for the primary canvas and **Charcoal** for elevated containers.

- **Atlas Red (#ff0000):** Used sparingly for critical system errors, Atlas-tier discoveries, and high-priority destructive actions.
- **Portal Cyan (#00ffff):** The primary functional accent. Used for interactive elements, active states, and successful scans.
- **Discovery Gold (#ffcc00):** Reserved for rare milestones, exotic planet classifications, and "S-Class" status indicators.
- **Status Colors:** 
    - *Confirmed:* Portal Cyan.
    - *Draft:* Neutral Silver-Grey.
    - *Incomplete:* Discovery Gold.

## Typography
This design system utilizes a three-font hierarchy to balance futuristic personality with legibility.

- **Space Grotesk** is used for headlines and primary data points, providing a technical yet modern geometric feel.
- **Inter** handles all body copy and long-form logs to ensure high readability during extended sessions.
- **Space Mono** is the "Technical" layer. It is used exclusively for coordinates (Glyphs), ship stats, unit costs, and metadata labels. 

All labels should be uppercase with wide tracking to mimic a diagnostic readout.

## Layout & Spacing
The layout follows a **Fluid Grid** model with a heavy emphasis on vertical stacks for mobile-first Android usage. A technical grid overlay (10% opacity cyan lines) should be visible in the background of primary views to reinforce the "HUD" aesthetic.

- **Navigation:** A slim Navigation Rail is preferred for large-screen tablets, while a bottom-anchored persistent menu is used for mobile.
- **Padding:** Containers use tight internal padding (12px) to maximize screen real estate for data-heavy planet logs.
- **Safe Areas:** Ensure a 16px horizontal margin is maintained on all mobile screens to prevent content from hitting the device edge.

## Elevation & Depth
Depth is not achieved through traditional drop shadows but through **Tonal Layering** and **Backdrop Blurs**.

- **Level 0 (Base):** Deep Space Navy (#0a0c14).
- **Level 1 (Cards/Panels):** Semi-transparent Charcoal with a 12px Backdrop Blur. 
- **Level 2 (Modals/Popovers):** Higher opacity surface with a subtle 1px inner stroke in Cyan (#00ffff) at 20% opacity.
- **Glow Effects:** Active elements (like the 'Analyze Capture' button) should have an outer glow (box-shadow: 0 0 15px) using their respective accent color to simulate light emission.

## Shapes
The shape language is "Soft-Industrial." While the grid is rigid, elements have a slight 0.25rem corner radius to avoid the harshness of 90-degree angles, ensuring the UI feels modern and premium. 

**Special Case:** Glyph icons and Ship Class chips should use 0px (sharp) corners or a 45-degree "clipped" corner (beveled) to emphasize the sci-fi aesthetic.

## Components

### Buttons
- **Primary (Analyze Capture):** High-contrast Portal Cyan background with black text. On hover/active, it triggers a "pulsing" outer glow.
- **Secondary:** Transparent background with a 1px Cyan border.
- **Ghost:** No border, Space Mono text with a trailing `>_` character.

### Structured Cards (Planets/Ships)
Cards feature a technical header with a vertical accent bar on the left (Red for Atlas, Gold for Exotic). Use a background grid texture and display coordinates in the `code-sm` font style at the bottom-right of the card.

### Status Chips
- **Confirmed:** Solid Portal Cyan with black `label-caps` text.
- **Draft:** Ghost-style with a dashed white border.
- **Incomplete:** Solid Discovery Gold with black `label-caps` text.

### Input Fields
Inputs are bottom-border only (2px thick). When focused, the border glows Cyan and a small technical "Scanning..." label appears in the top-right corner of the field.

### Navigation Rail
A narrow vertical bar on the left (or bottom for mobile) using glyph-style icons. The active state is indicated by a vertical Cyan light-bar and a slight increase in icon opacity.