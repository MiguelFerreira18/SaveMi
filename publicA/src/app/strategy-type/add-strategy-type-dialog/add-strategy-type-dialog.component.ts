import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-add-strategy-type-dialog',
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-strategy-type-dialog.component.html',
  styleUrl: './add-strategy-type-dialog.component.css',
  providers: [provideNativeDateAdapter()],
})
export class AddStrategyTypeDialogComponent {
  strategyType = new FormGroup({
    name: new FormControl(''),
    description: new FormControl(''),
  });

  constructor(private dialogRef: MatDialogRef<AddStrategyTypeDialogComponent>) {}

  submit() {
    this.dialogRef.close(this.strategyType.value);
  }
}
