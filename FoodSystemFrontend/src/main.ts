import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';


platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));

window.addEventListener('beforeunload', (event) => {
  if (performance.navigation.type !== performance.navigation.TYPE_RELOAD) {
    localStorage.clear();
  }
});
