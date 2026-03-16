import '@testing-library/jest-dom'

// Polyfill AnimationEvent for jsdom, which doesn't implement CSS animation APIs.
// Without this, React's vendor-prefix detection maps `onAnimationEnd` to
// `webkitAnimationEnd`, making it impossible to trigger via standard test helpers.
if (!('AnimationEvent' in window)) {
  ;(window as any).AnimationEvent = Event
}
