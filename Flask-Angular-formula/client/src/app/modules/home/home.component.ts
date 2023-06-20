import { Component, OnInit } from '@angular/core';
import { DataService } from '../../services/data.service';
import {Functdata} from '../../models/functdata';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  functionList;
  formula: string;

  func: string;
  result: string;

  constructor(private dataService: DataService) { }

  ngOnInit() {
    this.dataService.getList().subscribe((data: any[]) => {
      console.log(data);
      this.functionList = data;
    });
  }

  getId(id: string) {
    this.dataService.searchById(id).subscribe((data: Functdata) => {
      console.log(data);
    });
  }

  calculate(formula: string) {
    this.result = '';
    this.func = '';
    this.dataService.calculate(encodeURIComponent(formula)).subscribe((data: CalcResult) => {
      this.result = data.result;
      this.func = data.formula;
    });
  }

}


export interface CalcResult {
  result: string;
  formula: string;
}
