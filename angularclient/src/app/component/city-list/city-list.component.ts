import {Component, OnInit} from '@angular/core';
import {City} from 'src/app/model/city';
import {CityService} from 'src/app/service/city.service';

@Component({
  selector: 'app-city-list',
  templateUrl: './city-list.component.html',
  styleUrls: ['./city-list.component.css']
})
export class CityListComponent implements OnInit {

  cities: City[] = [];
  currentCity: City = {};
  currentIndex = -1;
  name = '';

  page = 1;
  count = 0;
  pageSize = 5;
  pageSizes = [5, 10, 20, 50, 100];

  constructor(private cityService: CityService) { }

  ngOnInit(): void {
    this.getCities();
  }

  getRequestParams(searchName: string, page: number, pageSize: number): any {
    let params: any = {};

    if (searchName) {
      params[`name`] = searchName;
    }

    if (page) {
      params[`page`] = page - 1;
    }

    if (pageSize) {
      params[`size`] = pageSize;
    }

    return params;
  }

  getCities(): void {
    const params = this.getRequestParams(this.name, this.page, this.pageSize);

    this.cityService.findAll(params)
      .subscribe(
        response => {
          const { content, totalElements } = response;
          this.cities = content;
          this.count = totalElements;
          console.log(response);
        },
        error => {
          console.log(error);
        });
  }

  handlePageChange(event: number): void {
    this.page = event;
    this.getCities();
  }

  handlePageSizeChange(event: any): void {
    this.pageSize = event.target.value;
    this.page = 1;
    this.getCities();
  }

  refreshList(): void {
    this.getCities();
    this.currentCity = {};
    this.currentIndex = -1;
  }

  setActiveCity(city: City, index: number): void {
    this.currentCity = city;
    this.currentIndex = index;
  }

  searchName(): void {
    this.page = 1;
    this.getCities();
  }
}
