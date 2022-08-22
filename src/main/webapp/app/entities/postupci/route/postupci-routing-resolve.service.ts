import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPostupci, Postupci } from '../postupci.model';
import { PostupciService } from '../service/postupci.service';

@Injectable({ providedIn: 'root' })
export class PostupciRoutingResolveService implements Resolve<IPostupci> {
  constructor(protected service: PostupciService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPostupci> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((postupci: HttpResponse<Postupci>) => {
          if (postupci.body) {
            return of(postupci.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Postupci());
  }
}
