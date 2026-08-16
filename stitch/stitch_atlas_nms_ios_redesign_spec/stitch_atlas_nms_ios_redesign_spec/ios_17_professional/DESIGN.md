---
name: iOS 17 Professional
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1b1b1b'
  surface-container: '#1f1f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353535'
  on-surface: '#e2e2e2'
  on-surface-variant: '#c0c6d6'
  inverse-surface: '#e2e2e2'
  inverse-on-surface: '#303030'
  outline: '#8b91a0'
  outline-variant: '#414754'
  surface-tint: '#aac7ff'
  primary: '#aac7ff'
  on-primary: '#003064'
  primary-container: '#3e90ff'
  on-primary-container: '#002957'
  inverse-primary: '#005db8'
  secondary: '#c8c6c8'
  on-secondary: '#303032'
  secondary-container: '#474649'
  on-secondary-container: '#b6b4b7'
  tertiary: '#c8c6c8'
  on-tertiary: '#303032'
  tertiary-container: '#919092'
  on-tertiary-container: '#29292b'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#d6e3ff'
  primary-fixed-dim: '#aac7ff'
  on-primary-fixed: '#001b3e'
  on-primary-fixed-variant: '#00468d'
  secondary-fixed: '#e4e2e4'
  secondary-fixed-dim: '#c8c6c8'
  on-secondary-fixed: '#1b1b1d'
  on-secondary-fixed-variant: '#474649'
  tertiary-fixed: '#e4e2e4'
  tertiary-fixed-dim: '#c8c6c8'
  on-tertiary-fixed: '#1b1b1d'
  on-tertiary-fixed-variant: '#474649'
  background: '#131313'
  on-background: '#e2e2e2'
  surface-variant: '#353535'
  system-orange: '#FF9F0A'
  system-yellow: '#FFD60A'
  separator: rgba(84, 84, 88, 0.6)
  text-primary: '#FFFFFF'
  text-secondary: rgba(235, 235, 245, 0.6)
  material-bar: rgba(22, 22, 23, 0.72)
typography:
  large-title:
    fontFamily: Inter
    fontSize: 34px
    fontWeight: '700'
    lineHeight: 41px
    letterSpacing: 0.37px
  headline:
    fontFamily: Inter
    fontSize: 17px
    fontWeight: '600'
    lineHeight: 22px
    letterSpacing: -0.41px
  body:
    fontFamily: Inter
    fontSize: 17px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: -0.41px
  footnote:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: -0.08px
  technical-data:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-screen: 16px
  padding-cell: 12px
  gutter-stack: 8px
  nav-bar-height: 44px
  tab-bar-height: 49px
---

## Brand & Style

The design system is a precise implementation of the **Apple Human Interface Guidelines (HIG)** for iOS 17, tailored for a high-performance network management environment. The style is **Corporate Modern**, prioritizing clarity, precision, and native familiarity. 

By leveraging a pure black background, the UI disappears into the hardware of OLED displays, allowing data and active controls to take center stage. The emotional response is one of absolute reliability and technical authority. The interface utilizes high-performance **Glassmorphism** for structural navigation elements and **Tonal Layering** for content organization, ensuring a deep sense of hierarchy and spatial awareness.

## Colors

This design system utilizes the official iOS 17 System Dark palette. 

- **Backgrounds:** Pure black (#000000) is the primary canvas. Grouped content sections use Secondary System Background (#1C1C1E) to create distinct visual groupings.
- **Interactive:** System Blue (#0A84FF) is the singular tint color for all primary actions, links, and active states.
- **Status:** Semantic colors are restricted to specific badges—System Orange for "Confirmed" states and System Yellow for "S-Class" status.
- **Typography:** White (#FFFFFF) provides maximum contrast for primary content, while Secondary Text uses a specific translucent variant to de-emphasize metadata and labels.
- **Dividers:** Separators must be thin and translucent to provide structure without visual clutter.

## Typography

The system uses **Inter** as the primary typeface, selected for its systematic neutrality and exceptional legibility on digital screens, serving as a professional counterpart to SF Pro.

For developer-centric data such as IP addresses, logs, and network strings, **JetBrains Mono** is employed to ensure character distinction (e.g., `0` vs `O`).

- **Hierarchy:** Use "Large Title" exclusively for the primary header of top-level views.
- **Body & Headline:** Both share a 17px base size to maintain a consistent reading rhythm, distinguished only by weight.
- **Scaling:** For mobile, Large Title scales down to Headline in the navigation bar upon scroll.

## Elevation & Depth

Depth is established through **Material Blurs** and **Tonal Layering**, rejecting traditional drop shadows in favor of physical layering.

- **Structural Layers:** Navigation and Tab bars utilize a `backdrop-filter: blur(25px)` with the `material-bar` color. This creates a "glass" effect where content scrolls beneath the bars.
- **Tonal Tiers:** Depth is communicated by color brightness. Level 0 is Black (#000000). Level 1 is the secondary background (#1C1C1E) for cells. Level 2 is the tertiary background (#2C2C2E) for active inputs or secondary overlays.
- **Modals:** Modal sheets appear with a 12px radius, sitting atop the background with a subtle dimming of the layers beneath them.

## Shapes

The shape language is strictly defined by functional roles to help users distinguish between containers and interactive elements.

- **Containers & Cells:** All list items and grouped containers use a **10px** corner radius.
- **Actionable Elements:** Buttons and Modal Sheets use a more pronounced **12px** radius to signal interactivity and prominence.
- **Pills:** Status badges and tags use a fully rounded (pill) radius for a soft, distinct look compared to structural UI.
- **Icons:** Use SF Symbols exclusively, ensuring the weight of the icon matches the surrounding typography weight (typically Regular or Semibold).

## Components

### Buttons
- **Primary Action:** Solid System Blue (#0A84FF) background with White text and a 12px corner radius.
- **Secondary/System:** Text-only buttons in System Blue, used primarily in Navigation Bars.

### Cells & Lists
- **Inset Grouped Style:** Cells use the #1C1C1E background with 12px internal padding and a 10px radius. 
- **Separators:** Use 0.5pt (1px) lines in `separator` color, inset from the left to align with the text label.

### Input Fields
- **Search & Text Inputs:** Use #2C2C2E background with a 10px corner radius. Placeholder text should use `text-secondary`.

### Status Badges
- **Confirmed:** System Orange text on a 15% opacity System Orange background.
- **S-Class:** System Yellow text on a 15% opacity System Yellow background.
- Always rendered in a full pill shape with Footnote typography.

### Selection Controls
- **Switches:** Standard iOS toggle style; green (#34C759) for the 'on' state.
- **Segmented Control:** #2C2C2E track background with a #636366 sliding indicator.