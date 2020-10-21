
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { SourceService } from '../source.service';
import { source } from '../sources.model';
 

@Component({
  selector: 'app-sources-add',
  templateUrl: './sources-add.component.html',
  styleUrls: ['./sources-add.component.css']
})
export class SourcesAddComponent implements OnInit {
  public sourceForm: FormGroup;
  public isSynology: boolean = false;
  public isConnecting: boolean = false;
  public connexionSuccess: boolean = false;
  public checkingFolder: boolean = false;
  public folderChecked: boolean = false;
  public error: string;
  public success: string;

  constructor(private location: Location, private sourceService: SourceService) { }
 
  ngOnInit() {
    this.sourceForm = new FormGroup({
      sourceType: new FormControl('', [Validators.required]),
      name: new FormControl('', [Validators.required, Validators.maxLength(20)]),
      host: new FormControl(''),
      port: new FormControl(''),
      userName: new FormControl(''),
      password: new FormControl(''),
      rootFolder: new FormControl('')
    });
  }
 
  public hasError = (controlName: string, errorName: string) =>{
    return this.sourceForm.controls[controlName].hasError(errorName);
  }
 
  public onCancel = () => {
    this.location.back();
  }
 
  public checkSource = (courseFormValue) => {
    if (this.sourceForm.valid && this.isSynology && !this.connexionSuccess) {
      this.isConnecting = true;
      this.tryConnexion(courseFormValue);
    } else if(this.sourceForm.valid && this.connexionSuccess && !this.folderChecked) {
        this.checkingFolder = true;
        this.checkingFolderExist(courseFormValue);
    } else if(this.folderChecked) {
      var s = new source(null, courseFormValue.name, courseFormValue.sourceType, courseFormValue.host, courseFormValue.port, courseFormValue.userName, courseFormValue.rootFolder);
      s['password']=courseFormValue.password;
      this.sourceService.save(s).subscribe(response => {
          console.log("saved");
      });
    }
  }

  private checkingFolderExist(courseFormValue) {
    var s = new source(null, courseFormValue.name, courseFormValue.sourceType, courseFormValue.host, courseFormValue.port, courseFormValue.userName, courseFormValue.rootFolder);
    s['password']=courseFormValue.password;
    this.sourceService.checkFolder(s).subscribe(response => {
      if(response) {
        this.success="The folder is valid"
        this.error = undefined;
        this.folderChecked = true;
        this.sourceForm.get('rootFolder').disable()
      } else {
        this.success = undefined;
        this.error = "The folder is not valid or does not exist"
      }
      this.checkingFolder = false;
    })
  }

  private tryConnexion(courseFormValue) {
    var s = new source(null, courseFormValue.name, courseFormValue.sourceType, courseFormValue.host, courseFormValue.port, courseFormValue.userName, courseFormValue.rootFolder);
    s['password']=courseFormValue.password;
    this.sourceService.tryConnexion(s).subscribe(response => {
      if(response) {
        this.connexionSuccess = true;
        this.isConnecting = false;
        this.sourceForm.get('rootFolder').setValidators(Validators.required);
        this.error = undefined;
        this.success="Connected to synology"

      /*  this.sourceForm.get('host').disable()
        this.sourceForm.get('port').disable()
        this.sourceForm.get('userName').disable()
        this.sourceForm.get('password').disable()*/
      } else {
        this.isConnecting = false;
        this.success = undefined;
        this.error = "unnable to connect to Synology"
      }
    })
  }
 

  public sourceChange(event) {
    if(event.value == 'Synology') {
      this.isSynology = true;
      this.connexionSuccess = false;
      this.sourceForm.get('host').setValidators(Validators.required);
      this.sourceForm.get('port').setValidators(Validators.required);
      this.sourceForm.get('userName').setValidators(Validators.required);
      this.sourceForm.get('password').setValidators(Validators.required);
      this.sourceForm.get('rootFolder').clearValidators();
    } else {
      this.isSynology = false;
      this.connexionSuccess = true;
      this.sourceForm.get('host').clearValidators();
      this.sourceForm.get('port').clearValidators();
      this.sourceForm.get('userName').clearValidators();
      this.sourceForm.get('password').clearValidators();
      this.sourceForm.get('rootFolder').setValidators(Validators.required);
    }
  }

}
