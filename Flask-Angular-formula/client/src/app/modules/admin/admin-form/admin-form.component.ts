import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Functdata, FunctdataCommand} from '../../../models/functdata';
import {DataService} from '../../../services/data.service';

@Component({
  selector: 'app-admin-form',
  templateUrl: './admin-form.component.html',
  styleUrls: ['./admin-form.component.scss']
})
export class AdminFormComponent implements OnInit {

  command: FunctdataCommand;
  creation = false;

  constructor(@Inject(MAT_DIALOG_DATA) public functdata: Functdata, private dialog: MatDialog, private inputDialog: MatDialog,
              public dialogRef: MatDialogRef<AdminFormComponent>, private dataService: DataService, ) {
    this.command = FunctdataCommand.mapToCommand(functdata);
    this.creation = functdata.id == null;
  }

  ngOnInit(): void {

  }

  async save() {
    if (this.creation){
      this.functdata = await this.dataService.create(this.command).toPromise();
    }else{
      this.functdata = await this.dataService.update(this.command.id, this.command).toPromise();
    }
    console.log(this.functdata);
    this.dialogRef.close(true);
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
