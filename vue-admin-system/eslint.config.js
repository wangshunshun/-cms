import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import eslintPluginPrettier from 'eslint-plugin-prettier/recommended'

export default [
  {
    ignores: [
      'dist/**',
      'node_modules/**',
      '*.local',
      '*.log*',
      'public/**',
      'src/auto-imports.d.ts',
      'src/components.d.ts'
    ]
  },
  js.configs.recommended,
  ...pluginVue.configs['flat/recommended'],
  eslintPluginPrettier,
  {
    files: ['**/*.vue'],
    rules: {
      'vue/multi-word-component-names': 'off',
      'vue/no-v-html': 'off'
    }
  },
  {
    files: ['**/*.js', '**/*.vue'],
    rules: {
      'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
      'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
      'no-unused-vars': ['error', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],
      'prefer-const': 'error',
      'no-var': 'error'
    }
  }
]
