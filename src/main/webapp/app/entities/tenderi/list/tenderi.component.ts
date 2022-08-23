import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';

import { ITenderi } from '../tenderi.model';
import { TenderiService } from '../service/tenderi.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'jhi-tenderi',
  templateUrl: './tenderi.component.html',
})
export class TenderiComponent implements OnInit {
  active = 1;
  sifra?: any;
  constructor(protected activatedRoute: ActivatedRoute) {}
  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.sifra = params['sifra'];
    });
  }
}
