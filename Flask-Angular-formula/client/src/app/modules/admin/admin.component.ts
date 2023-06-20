import {Component, OnInit, ViewChild, AfterViewInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {DataService} from '../../services/data.service';
import {Functdata} from '../../models/functdata';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AdminFormComponent} from './admin-form/admin-form.component';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit, AfterViewInit {
  cols = ['id', 'name', 'function', 'description', 'actions'];
  displayedColumns: string[] = ['id', 'name', 'description', 'function'];

  @ViewChild(MatPaginator) paginator: MatPaginator;

  datasource;
  nametxt: string;
  listFunc: Functdata[] = [];

  constructor(private dataService: DataService, public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    this.dataService.getList().subscribe((data: Functdata[]) => {
      console.log(data);
      this.listFunc = data;
      this.datasource = new MatTableDataSource(this.listFunc);
      this.datasource.paginator = this.paginator;
    });
  }

  async createNew(){
    await this.edit(new Functdata());
  }


  async edit(func: Functdata) {
    const result = await this.dialog.open(AdminFormComponent,
      {width: '60%', data: func, panelClass: 'open-modalbox', disableClose: true})
       .afterClosed().toPromise();
    if (result) {
      await this.ngAfterViewInit();
    }
  }

  async delete(func: Functdata){
      await this.dataService.delete(func.id).toPromise();
      await this.ngAfterViewInit();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = filterValue.trim().toLowerCase();
  }


}

