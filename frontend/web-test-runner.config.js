import vite from 'vite-web-test-runner-plugin'

// NODE_ENV=test - Needed by "vite-web-test-runner-plugin"
process.env.NODE_ENV = 'test'
export default {
  plugins: [vite()],
  testRunnerHtml: (testFramework) => `
    <html>
      <head>
        <script type="module">
          // Note: globals expected by @testing-library/react
          window.global = window;
          window.process = { env: {} };
          // Note: adapted from https://github.com/vitejs/vite/issues/1984#issuecomment-778289660
          // Note: without this you'll run into https://github.com/vitejs/vite-plugin-react/pull/11#discussion_r430879201
          window.__vite_plugin_react_preamble_installed__ = true;
        </script>
        <script type="module" src="${testFramework}"></script>
      </head>
    </html>
  `
}
