---
name: Atlas NMS Explorer
colors:
  surface: '#121416'
  surface-dim: '#121416'
  surface-bright: '#38393c'
  surface-container-lowest: '#0c0e10'
  surface-container-low: '#1a1c1e'
  surface-container: '#1e2022'
  surface-container-high: '#282a2c'
  surface-container-highest: '#333537'
  on-surface: '#e2e2e5'
  on-surface-variant: '#bcc9c6'
  inverse-surface: '#e2e2e5'
  inverse-on-surface: '#2f3133'
  outline: '#869391'
  outline-variant: '#3d4947'
  surface-tint: '#5fd9cc'
  primary: '#5fd9cc'
  on-primary: '#003733'
  primary-container: '#2bb1a5'
  on-primary-container: '#003e39'
  inverse-primary: '#006a62'
  secondary: '#ffb4a5'
  on-secondary: '#611205'
  secondary-container: '#802918'
  on-secondary-container: '#ff9a85'
  tertiary: '#b9ccb0'
  on-tertiary: '#253421'
  tertiary-container: '#92a48a'
  on-tertiary-container: '#2a3a26'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#7ef6e9'
  primary-fixed-dim: '#5fd9cc'
  on-primary-fixed: '#00201d'
  on-primary-fixed-variant: '#00504a'
  secondary-fixed: '#ffdad3'
  secondary-fixed-dim: '#ffb4a5'
  on-secondary-fixed: '#3e0500'
  on-secondary-fixed-variant: '#802918'
  tertiary-fixed: '#d5e8cb'
  tertiary-fixed-dim: '#b9ccb0'
  on-tertiary-fixed: '#101f0d'
  on-tertiary-fixed-variant: '#3b4b36'
  background: '#121416'
  on-background: '#e2e2e5'
  surface-variant: '#333537'
typography:
  display-lg:
    fontFamily: Space Grotesk
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-md:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 28px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 40px
  gutter: 16px
  margin: 24px
---

## Brand & Style
The design system adopts a **Grounded Material** aesthetic, pivoting away from sci-fi tropes toward a utilitarian, high-precision instrument feel. It is designed for researchers and analysts who require clarity over spectacle.

The personality is professional, reliable, and "matte." It emphasizes structural integrity through deliberate information density and structural alignment. By utilizing a "Tactile-Technical" approach, the interface moves from a digital fantasy to a functional dashboard, prioritizing long-form readability and data integrity.

**Design Principles:**
- **Matte over Gloss:** No transparency, blurs, or light-leak effects.
- **Utilitarian Precision:** Every element serves a functional purpose; decoration is replaced by structural logic.
- **Atmospheric Professionalism:** A sophisticated palette that evokes deep-space observation without relying on neon cliches.

## Colors
The color system moves from high-intensity saturation to a sophisticated, earthy palette. The background uses a deep charcoal base to reduce eye strain, while accents are pulled from natural, mineral-inspired tones.

- **Primary (Teal):** A muted, professional teal used for primary actions and data highlights.
- **Secondary (Terracotta):** A desaturated clay-red for alerts, critical warnings, and significant markers.
- **Tertiary (Sage):** A soft olive-grey used for secondary data streams and environmental metadata.
- **Neutral (Charcoal & Stone):** The foundation of the UI, using tonal shifts to define hierarchy rather than shadows.

All color combinations must pass WCAG AA standards for contrast (4.5:1 for normal text).

## Typography
Typography is treated as a technical spec. **Space Grotesk** provides a modern, geometric structure for headlines but is kept at a tighter tracking to feel authoritative. **Hanken Grotesk** handles the body text for its exceptional legibility in dense data environments. **JetBrains Mono** is introduced for metadata and coordinates to emphasize the "Explorer" nature of the interface.

- **Headlines:** Use sentence case. High weight for emphasis, never all-caps.
- **Technical Labels:** Use all-caps with increased letter-spacing in monospaced font for small data points.
- **Scalability:** Large displays are reduced for mobile to maintain a single-column reading rhythm.

## Layout & Spacing
The layout follows a rigid 4px baseline grid. This ensures that every element feels locked into a technical schematic.

- **Grid:** A 12-column fluid grid for desktop, transitioning to 4-column for mobile.
- **Rhythm:** Use "MD" (16px) for standard gaps between logical groups and "SM" (8px) for elements within a group.
- **Sectioning:** Content is separated by background tonal shifts (e.g., a slightly lighter charcoal for a sidebar) and 1px borders rather than white space alone.

## Elevation & Depth
Depth is communicated through **Tonal Layering** and **Subtle Outlines**. In this design system, higher elevation means a lighter background color, not a bigger shadow.

- **Surface Levels:** 
  - Level 0 (Background): `#121416`
  - Level 1 (Cards/Containers): `#1C1F22`
  - Level 2 (Modals/Popovers): `#262A2E`
- **Borders:** 1px solid `#31363B` is the primary separator for all interactive elements.
- **Shadows:** Only used for top-level modals to separate them from the interface. Use a 24px blur, 0% offset, and 40% opacity of the background color (nearly invisible, purely for edge definition).

## Shapes
Shapes are "Soft" but disciplined. The 0.25rem (4px) corner radius provides a hint of approachability while maintaining the professional feel of a precision instrument.

- **Standard Elements:** 4px radius (Buttons, Inputs, Cards).
- **Secondary Elements:** 0px radius (Large panel dividers, screen-edge containers) to emphasize a "built-in" structural feel.
- **Icons:** Use a 2px stroke width with squared-off ends to match the typographic terminals of Space Grotesk.

## Components
Consistent component styling reinforces the grounded, utilitarian nature of the design system.

- **Buttons:**
  - **Primary:** Solid teal background, dark charcoal text. No gradients.
  - **Secondary:** Transparent background with a 1px teal border.
  - **States:** Hover should be a simple 10% brightness increase. No "glow" or outer shadows.
- **Input Fields:** 1px border with a slightly darker inset background. Focus states use a 2px interior border of the primary teal color.
- **Cards:** No shadows. Use Level 1 background (`#1C1F22`) and a 1px border. Title areas within cards should have a subtle background header strip.
- **Chips/Status:** Use the Secondary (Terracotta) or Tertiary (Sage) colors as small, circular indicators or low-saturation background pills.
- **Lists:** Use subtle 1px horizontal dividers. Ensure high contrast between the list item title (Headline font) and metadata (Monospace font).
- **Data Visualizations:** Use flat fills. Lines should be 1.5px thick. Replace glowing line charts with solid, high-contrast paths.