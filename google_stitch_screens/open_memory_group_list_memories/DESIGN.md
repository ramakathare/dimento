# Design System Specification: The Botanical Minimalist

## 1. Overview & Creative North Star
The Creative North Star for this design system is **"The Living Gallery."** 

We are moving away from the rigid, boxy constraints of traditional Material 3 and toward an editorial experience that feels organic, airy, and premium. By leaning into extreme corner radii and tonal depth rather than structural lines, we create a UI that feels grown, not manufactured. 

The "Modern Android" aesthetic is achieved here through **Intentional Asymmetry**. We break the "template" look by using generous white space and overlapping elements—where a high-resolution image might bleed off the edge of a `3rem` rounded container, or a floating action button sits slightly offset from a grid line. This system prioritizes breathing room over information density to convey a sense of calm and high-end curation.

---

## 2. Colors & Surface Philosophy
The palette is rooted in botanical greens and mineral neutrals, designed to reduce cognitive load.

### The "No-Line" Rule
**Strict Mandate:** Designers are prohibited from using 1px solid borders for sectioning. 
Boundaries must be defined solely through background color shifts. For example, a `surface-container-low` (#f1f4f3) card should sit on a `surface` (#f8faf9) background. Contrast is achieved through tone, not strokes.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers, like stacked sheets of fine, heavy-weight paper.
*   **Base Layer:** `surface` (#f8faf9)
*   **Secondary Content:** `surface-container-low` (#f1f4f3)
*   **Interactive Cards:** `surface-container-lowest` (#ffffff)
*   **Elevated Overlays:** `surface-bright` (#f8faf9)

### The "Glass & Gradient" Rule
To elevate the app beyond a standard utility, use **Glassmorphism** for floating headers or navigation bars. Use `surface` at 80% opacity with a `20px` backdrop blur. 
**Signature Textures:** For primary CTAs, do not use flat colors. Apply a subtle linear gradient from `primary` (#1c6d25) to `primary_dim` (#096119) at a 135° angle to provide "soul" and depth.

---

## 3. Typography: The Editorial Voice
We utilize a pairing of **Manrope** (Display/Headline) and **Inter** (Body/Label) to create an authoritative yet approachable hierarchy.

*   **Display & Headlines (Manrope):** Use `display-lg` (3.5rem) and `headline-md` (1.75rem) with tight letter spacing (-0.02em). These are your "Editorial Anchors."
*   **Body & Titles (Inter):** Use `body-lg` (1rem) for all long-form text. Inter’s tall x-height ensures readability against the soft green backgrounds.
*   **Hierarchy Note:** Use `on_surface_variant` (#596060) for secondary body text to create a clear visual distinction from the primary `on_surface` (#2d3433) titles.

---

## 4. Elevation & Depth
Depth in this system is a result of light and shadow, not lines.

### Ambient Shadows
When a card requires a "floating" effect, use an **Ambient Shadow**:
*   **Blur:** 32px to 48px
*   **Spread:** -4px
*   **Color:** 6% opacity of `on_surface` (#2d3433)
*   **Y-Offset:** 8px

### The "Ghost Border" Fallback
If a border is required for accessibility (e.g., in high-glare environments), use a **Ghost Border**:
*   Token: `outline_variant` (#acb3b2)
*   Opacity: **15% max**
*   Weight: 1px

### Layering Principle
Stacking is the primary method of organization. Place a `surface-container-highest` (#dde4e3) element behind a `primary_container` (#9df197) element to create natural, soft-touch depth without any CSS box-shadow properties.

---

## 5. Components

### Buttons
*   **Primary:** Gradient fill (`primary` to `primary_dim`), `full` roundedness (9999px), `on_primary` (#eaffe2) text.
*   **Secondary:** `secondary_container` (#d6e8ce) fill, no shadow, `on_secondary_container` (#465643) text.
*   **Tertiary:** Ghost style. No background, `primary` text, with a `0.5rem` padding for a generous hit area.

### Cards & Lists
*   **Corner Radius:** Always use `lg` (2rem/32px) or `xl` (3rem/48px) for main content containers.
*   **Separation:** Forbid divider lines. Separate list items using `spacing-4` (1.4rem) of vertical white space or by alternating between `surface` and `surface_container_low`.

### Input Fields
*   **Style:** Filled, not outlined.
*   **Background:** `surface_container_high` (#e4e9e8).
*   **Shape:** `md` (1.5rem/24px) rounded corners.
*   **Focus State:** Transition background to `primary_container` (#9df197) with a `primary` text cursor.

### Signature Component: The "Floating Sheet"
A bottom sheet or modal that doesn't touch the screen edges. It uses `rounded-xl`, sits on an ambient shadow, and utilizes `surface_container_lowest` to pop against the dimmed background.

---

## 6. Do’s and Don’ts

### Do
*   **Do** use asymmetrical margins. For example, a left margin of `spacing-6` and a right margin of `spacing-8` for hero text.
*   **Do** lean into the "Highly Rounded" look. If a button can be a pill shape, make it a pill.
*   **Do** use `primary_fixed_dim` (#90e28a) for subtle background highlights behind iconography.

### Don't
*   **Don't** use 100% black (#000000). Use `on_surface` (#2d3433) for all "black" text.
*   **Don't** use standard 4px or 8px corners. Anything less than `1rem` (16px) is prohibited unless it's a checkbox.
*   **Don't** use dividers. If you feel you need a line to separate content, you actually need more white space (refer to the `Spacing Scale`).