import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-add-currency-dialog',
  imports: [
    MatDialogModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-currency-dialog.component.html',
  styleUrl: './add-currency-dialog.component.css',
})
export class AddCurrencyDialogComponent {
  currency = new FormGroup({
    name: new FormControl(''),
    symbol: new FormControl(''),
  });

  constructor(private dialogRef: MatDialogRef<AddCurrencyDialogComponent>) {}

  submit() {
    this.dialogRef.close(this.currency.value);
  }
}
