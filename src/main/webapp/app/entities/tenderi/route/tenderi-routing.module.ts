import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TenderiComponent } from '../list/tenderi.component';
import { TenderiDetailComponent } from '../detail/tenderi-detail.component';
import { TenderiRoutingResolveService } from './tenderi-routing-resolve.service';

const tenderiRoute: Routes = [
  {
    path: '',
    component: TenderiComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TenderiDetailComponent,
    resolve: {
      tenderi: TenderiRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tenderiRoute)],
  exports: [RouterModule],
})
export class TenderiRoutingModule {}
