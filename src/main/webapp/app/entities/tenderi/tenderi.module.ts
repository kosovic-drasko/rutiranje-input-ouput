import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TenderiComponent } from './list/tenderi.component';
import { TenderiDetailComponent } from './detail/tenderi-detail.component';
import { TenderiRoutingModule } from './route/tenderi-routing.module';

@NgModule({
  imports: [SharedModule, TenderiRoutingModule],
  declarations: [TenderiComponent, TenderiDetailComponent],
})
export class TenderiModule {}
