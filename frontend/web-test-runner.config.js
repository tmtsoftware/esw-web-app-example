import plugin from '@snowpack/web-test-runner-plugin'
import { defaultReporter } from '@web/test-runner'
// eslint-disable-next-line import/no-unresolved
import ConsoleReporter from './ConsoleReporter.js'

// NODE_ENV=test - Needed by "@snowpack/web-test-runner-plugin"
process.env.NODE_ENV = 'test'
export default {
  testFramework: {
    config: {
      timeout: '3000'
    }
  },
  reporters: [defaultReporter({ reportTestResults: true, reportTestProgress: true }), ConsoleReporter()],
  plugins: [plugin()]
}
