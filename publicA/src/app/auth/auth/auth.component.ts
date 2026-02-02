import { Component, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../auth.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SignIn, SignUp } from '../../shared/models/user.model';
import { err, fold, ok, Result } from '../../shared/Monads';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth',
  imports: [MatFormFieldModule, MatInputModule, MatButtonModule, ReactiveFormsModule],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css',
})
export class AuthComponent {
  private authService: AuthService = inject(AuthService);
  private router: Router = inject(Router);

  isLogin = true;
  signInUser = new FormGroup({
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  signUpUser = new FormGroup({
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    name: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    repeatPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  signIn() {
    if (this.signInUser.valid) {
      const signIn: SignIn = {
        email: this.signInUser.controls.email.value,
        password: this.signInUser.controls.password.value,
      };

      this.authService.authenticate(signIn).subscribe({
        next: (user) => {
          localStorage.setItem('user', JSON.stringify(user));
          this.resetForms();
          this.router.navigate(['/']);
        },
        error: (err) => console.log(`Login failed ${err}`),
      });
    }
  }

  signUp() {
    if (this.signUpUser.valid) {
      const signUp: SignUp = {
        name: this.signUpUser.controls.name.value,
        email: this.signUpUser.controls.email.value,
        password: this.signUpUser.controls.password.value,
        repeatPassword: this.signUpUser.controls.repeatPassword.value,
      };

      this.authService.signUp(signUp).subscribe({
        next: (_) => {
          this.resetForms();
          this.isLogin = true;
        },
        error: (err) => console.log(`Login failed ${err}`),
      });
    }
  }
  private resetForms() {
    this.signUpUser.reset();
    this.signInUser.reset();
  }

  toggleLogin() {
    this.isLogin = !this.isLogin;
  }
}
