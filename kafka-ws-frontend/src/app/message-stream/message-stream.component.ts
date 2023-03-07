import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { Observable, of, Subject } from 'rxjs';
import { catchError, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-message-stream',
  templateUrl: './message-stream.component.html'
})
export class MessageStreamComponent implements OnInit, OnDestroy {

  myForm: FormGroup;

  uploadInfo: string;
  messages: string[];
  files : File[]

  private destroy$ = new Subject();

  constructor(private frmBuilder: FormBuilder,
    private http: HttpClient,
    private rxStompService: RxStompService) {

  }

  ngOnInit(): void {
    this.messages = [];
  }

  ngOnDestroy(): void {
    this.destroy$.next(null);
    this.destroy$.unsubscribe();
  }

  uploadFiles = () => {
    const formData: FormData = new FormData()
    this.files.forEach((file) => { formData.append('files[]', file);});
    this.http.post(`/api/kafka/upload`, formData, { observe: 'response' })
      .pipe(
        catchError(this.handleError.bind(this)),
        takeUntil(this.destroy$)
      ).subscribe((resp: HttpResponse<any>) => {
        this.files = [];
        this.uploadInfo = "";
      });
  }

  handleUpload = (e) => {
    this.files = Array.from(e.target.files) || []
    if (this.files.length === 1){
      this.uploadInfo = this.files[0].name
    } else {
      this.uploadInfo = `${this.files.length} files selected`
    }
  }


  private handleError(error: HttpErrorResponse): Observable<any> {
    return of(null);
  }

}
