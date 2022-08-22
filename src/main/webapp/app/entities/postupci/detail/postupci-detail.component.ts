import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPostupci } from '../postupci.model';

@Component({
  selector: 'jhi-postupci-detail',
  templateUrl: './postupci-detail.component.html',
})
export class PostupciDetailComponent implements OnInit {
  postupci: IPostupci | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ postupci }) => {
      this.postupci = postupci;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
