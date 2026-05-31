import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toasts: Toast[] = [];
  private toastSubject = new Subject<Toast[]>();
  toasts$ = this.toastSubject.asObservable();
  private nextId = 0;

  show(message: string, type: Toast['type'] = 'info', duration = 4000) {
    const toast: Toast = { id: this.nextId++, message, type };
    this.toasts.push(toast);
    this.toastSubject.next([...this.toasts]);

    setTimeout(() => this.dismiss(toast.id), duration);
  }

  dismiss(id: number) {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.toastSubject.next([...this.toasts]);
  }
}
