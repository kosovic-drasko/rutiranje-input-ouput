import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TenderiComponent } from '../list/tenderi.component';
import { TenderiDetailComponent } from '../detail/tenderi-detail.component';
import { TenderiRoutingResolveService } from './tenderi-routing-resolve.service';
import { PonudeComponent } from '../../ponude/list/ponude.component';
import { PostupciComponent } from '../../postupci/list/postupci.component';

const tenderiRoute: Routes = [
  {
    path: '',
    component: TenderiComponent,
    canActivate: [UserRouteAccessService],
    // children: [
    //   {
    //     path: 'ponude',
    //     component: PonudeComponent,
    //   },
    //   {
    //     path: 'postupci',
    //     component: PostupciComponent,
    //   },
    // ],
  },
  {
    path: ':id',
    component: TenderiComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(tenderiRoute)],
  exports: [RouterModule],
})
export class TenderiRoutingModule {}
