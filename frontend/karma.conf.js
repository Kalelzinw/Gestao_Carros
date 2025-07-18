module.exports = function(config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', 'karma-typescript'],
    files: [
            'src/services/**/*.spec.ts' // Arquivos de teste em servi
    ],
    exclude: [],
    preprocessors: {
      'src/apps/**/*.spec.ts': ['karma-typescript'],  // Pré-processador para TypeScript em apps
      'src/services/**/*.spec.ts': ['karma-typescript']  // Pré-processador para TypeScript em services
    },

       reporters: ['progress'], // Relatório do progresso dos testes
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    concurrency: Infinity,
    plugins: [
      'karma-chrome-launcher',
      'karma-jasmine',
      'karma-typescript' // Plugin para TypeScript
    ]
  });
};
