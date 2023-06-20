export class Functdata {
  id: string;
  name: string;
  description: string;
  function: string;
}


export class FunctdataCommand {
  id: string;
  name: string;
  description: string;
  function: string;

  public static mapToCommand(functdata: Functdata): FunctdataCommand {
    return {
      id: functdata.id,
      name: functdata.name,
      description: functdata.description,
      function: functdata.function
    };
  }
}
