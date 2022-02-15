import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CityListComponent} from './component/city-list/city-list.component';
import {CityFormComponent} from './component/city-form/city-form.component';

const routes: Routes = [
  { path: 'cities', component: CityListComponent },
  { path: 'edit-city', component: CityFormComponent },
  { path: '', redirectTo: 'cities', pathMatch: 'full' },
  // { path: 'cities/:id', component: CityFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
