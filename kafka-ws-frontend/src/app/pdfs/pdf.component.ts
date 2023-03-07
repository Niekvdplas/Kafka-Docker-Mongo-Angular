import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'pdf',
    templateUrl: './pdf.component.html',
})
export class PDFComponent {
    messages: string[];
    private destroy$ = new Subject();

    public isSortedUp : boolean = true;

    constructor(
        private http: HttpClient,
        private rxStompService: RxStompService) {

    }

    dataURItoBlob(dataURI, format: string) {
        const byteString = window.atob(dataURI);
        const arrayBuffer = new ArrayBuffer(byteString.length);
        const int8Array = new Uint8Array(arrayBuffer);
        for (let i = 0; i < byteString.length; i++) {
            int8Array[i] = byteString.charCodeAt(i);
        }
        const blob = new Blob([int8Array], { type: format });
        return blob;
    }

    ngOnInit(): void {
        this.messages = [];

        this.rxStompService.watch('/topic/pdf')
            .pipe(
                takeUntil(this.destroy$)
            ).subscribe((message: Message) => {
                var mess = JSON.parse(message.body)
                this.messages.push(mess);
            });


        this.http.get('/api/kafka/retrieve').subscribe((resp: HttpResponse<any>) => {
            for (let item in resp){
                this.messages.push(resp[item])
            }
          })
    }

    compare( a, b) {
        if ( a.created > b.created ){
          return -1;
        }
        if ( a.created < b.created ){
          return 1;
        }
        return 0;
      }

    sort() : void {
        this.isSortedUp = !this.isSortedUp;
        this.messages.sort(this.compare)
        if (this.isSortedUp){
            this.messages.reverse();
        }
    }

    open(content, format) : void {
        var blob = this.dataURItoBlob(content, format)
        var fileURL = URL.createObjectURL(blob);
        window.open(fileURL);
    }



    ngOnDestroy(): void {
        this.destroy$.next(null);
        this.destroy$.unsubscribe();
    }


}