package io.javabrains.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DiscoveryClient discoveryClient;

//	@Autowired
//	private WebClient.Builder webClientBuilder;

	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

		discoveryClient.getServices().stream() //
			.forEach(System.out::println); //
		
		UserRating userRatings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId,
				UserRating.class);

		List<CatalogItem> catalogItems = userRatings.getRatings().stream() //
				.map(rating -> {
					Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(),
							Movie.class); //
					return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating()); //
				}) //
				.collect(Collectors.toList());

		return catalogItems;

	}
}

/*
 * List<CatalogItem> catalogItems = ratings.stream() // .map(rating -> { Movie
 * movie = webClientBuilder.build() // .get() //
 * .uri("http://localhost:8082/movies/" + rating.getMovieId()) // .retrieve() //
 * .bodyToMono(Movie.class) // .block(); // return new
 * CatalogItem(movie.getName(), movie.getDescription(), rating.getRating()); //
 * 
 * }) // .collect(Collectors.toList()); //
 */
