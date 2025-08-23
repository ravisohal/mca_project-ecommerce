/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}"
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ["Inter", "ui-sans-serif", "system-ui", "Segoe UI", "Roboto", "Helvetica", "Arial", "sans-serif"]
      },
      boxShadow: {
        card: "0 8px 24px rgba(0,0,0,0.08)",
        cardHover: "0 16px 32px rgba(0,0,0,0.12)"
      },
      borderRadius: {
        xl: "1rem",
        "2xl": "1.5rem"
      }
    },
  },
  plugins: [],
};
