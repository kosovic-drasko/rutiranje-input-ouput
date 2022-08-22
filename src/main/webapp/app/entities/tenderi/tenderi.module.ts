import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TenderiComponent } from './list/tenderi.component';
import { TenderiDetailComponent } from './detail/tenderi-detail.component';
import { TenderiRoutingModule } from './route/tenderi-routing.module';
import { PonudeModule } from '../ponude/ponude.module';
import { SpecifikacijeModule } from '../specifikacije/specifikacije.module';
import { PostupciModule } from '../postupci/postupci.module';

@NgModule({
  imports: [SharedModule, TenderiRoutingModule, PonudeModule, SpecifikacijeModule, PostupciModule],
  declarations: [TenderiComponent, TenderiDetailComponent],
})
export class TenderiModule {}
