import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITenderi, Tenderi } from '../tenderi.model';
import { TenderiService } from '../service/tenderi.service';

@Injectable({ providedIn: 'root' })
export class TenderiRoutingResolveService implements Resolve<ITenderi> {
  constructor(protected service: TenderiService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITenderi> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tenderi: HttpResponse<Tenderi>) => {
          if (tenderi.body) {
            return of(tenderi.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Tenderi());
  }
}
