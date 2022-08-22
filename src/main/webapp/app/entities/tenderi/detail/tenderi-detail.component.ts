import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITenderi } from '../tenderi.model';

@Component({
  selector: 'jhi-tenderi-detail',
  templateUrl: './tenderi-detail.component.html',
})
export class TenderiDetailComponent implements OnInit {
  tenderi: ITenderi | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenderi }) => {
      this.tenderi = tenderi;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
