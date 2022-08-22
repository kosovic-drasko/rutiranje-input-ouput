import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PostupciComponent } from '../list/postupci.component';
import { PostupciDetailComponent } from '../detail/postupci-detail.component';
import { PostupciUpdateComponent } from '../update/postupci-update.component';
import { PostupciRoutingResolveService } from './postupci-routing-resolve.service';

const postupciRoute: Routes = [
  {
    path: '',
    component: PostupciComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PostupciDetailComponent,
    resolve: {
      postupci: PostupciRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PostupciUpdateComponent,
    resolve: {
      postupci: PostupciRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PostupciUpdateComponent,
    resolve: {
      postupci: PostupciRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(postupciRoute)],
  exports: [RouterModule],
})
export class PostupciRoutingModule {}
