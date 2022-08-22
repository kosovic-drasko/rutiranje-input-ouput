export interface IPostupci {
  id?: number;
  sifraPostupka?: number | null;
  opis?: string | null;
}

export class Postupci implements IPostupci {
  constructor(public id?: number, public sifraPostupka?: number | null, public opis?: string | null) {}
}

export function getPostupciIdentifier(postupci: IPostupci): number | undefined {
  return postupci.id;
}
