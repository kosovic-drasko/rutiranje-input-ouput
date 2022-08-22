import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tenderi',
        data: { pageTitle: 'jhipsterApp.tenderi.home.title' },
        loadChildren: () => import('./tenderi/tenderi.module').then(m => m.TenderiModule),
      },
      {
        path: 'postupci',
        data: { pageTitle: 'jhipsterApp.postupci.home.title' },
        loadChildren: () => import('./postupci/postupci.module').then(m => m.PostupciModule),
      },
      {
        path: 'ponude',
        data: { pageTitle: 'jhipsterApp.ponude.home.title' },
        loadChildren: () => import('./ponude/ponude.module').then(m => m.PonudeModule),
      },
      {
        path: 'specifikacije',
        data: { pageTitle: 'jhipsterApp.specifikacije.home.title' },
        loadChildren: () => import('./specifikacije/specifikacije.module').then(m => m.SpecifikacijeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
