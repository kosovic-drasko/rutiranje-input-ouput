import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';

import { ITenderi } from '../tenderi.model';
import { TenderiService } from '../service/tenderi.service';

@Component({
  selector: 'jhi-tenderi',
  templateUrl: './tenderi.component.html',
})
export class TenderiComponent implements OnInit {
  tenderis?: ITenderi[];
  isLoading = false;
  active = 1;
  constructor(protected tenderiService: TenderiService) {}

  loadAll(): void {
    this.isLoading = true;

    this.tenderiService.query().subscribe({
      next: (res: HttpResponse<ITenderi[]>) => {
        this.isLoading = false;
        this.tenderis = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ITenderi): number {
    return item.id!;
  }
}
