var webpack = require('webpack');

module.exports = {
  configureWebpack: {
    plugins: [
      new webpack.ProvidePlugin({
        'window.Quill': 'quill/dist/quill.js',
        'Quill': 'quill/dist/quill.js'
      }),
    ]
  },

  devServer: {
    proxy: {
      '/agtms': {
        target: 'http://0.0.0.0:7891',
        changeOrigin: true
      }
    }
  },

  lintOnSave: false
}