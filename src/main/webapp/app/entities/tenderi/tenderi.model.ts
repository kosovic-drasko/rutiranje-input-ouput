export interface ITenderi {
  id?: number;
}

export class Tenderi implements ITenderi {
  constructor(public id?: number) {}
}

export function getTenderiIdentifier(tenderi: ITenderi): number | undefined {
  return tenderi.id;
}
