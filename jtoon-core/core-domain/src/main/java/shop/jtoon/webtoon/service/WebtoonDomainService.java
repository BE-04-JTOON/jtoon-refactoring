package shop.jtoon.webtoon.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.jtoon.member.entity.Member;
import shop.jtoon.member.repository.MemberReader;
import shop.jtoon.webtoon.domain.SearchWebtoon;
import shop.jtoon.webtoon.domain.WebtoonDayOfWeeks;
import shop.jtoon.webtoon.domain.WebtoonDetail;
import shop.jtoon.webtoon.domain.WebtoonGenres;
import shop.jtoon.webtoon.domain.WebtoonInfo;
import shop.jtoon.webtoon.domain.WebtoonSchema;
import shop.jtoon.webtoon.entity.DayOfWeekWebtoon;
import shop.jtoon.webtoon.entity.GenreWebtoon;
import shop.jtoon.webtoon.entity.Webtoon;
import shop.jtoon.webtoon.entity.enums.DayOfWeek;
import shop.jtoon.webtoon.entity.enums.Genre;
import shop.jtoon.webtoon.repository.WebtoonReader;
import shop.jtoon.webtoon.repository.WebtoonWriter;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WebtoonDomainService {

	private final WebtoonManger webtoonManger;
	private final MemberReader memberReader;
	private final WebtoonWriter webtoonWriter;
	private final WebtoonReader webtoonReader;

	public void validateDuplicateTitle(String title) {
		webtoonManger.validationTitle(title);
	}

	@Transactional
	public Long createWebtoon(Long memberId, WebtoonInfo info, WebtoonGenres genres, WebtoonDayOfWeeks dayOfWeeks) {
		Member member = memberReader.read(memberId);

		Webtoon webtoon = info.toWebtoonEntity(member);
		List<DayOfWeekWebtoon> dayOfWeekWebtoons = dayOfWeeks.toDayOfWeekWebtoonEntity(webtoon);
		List<GenreWebtoon> genreWebtoons = genres.toGenreWebtoonEntity(webtoon);

		webtoonWriter.createWebtoon(webtoon, dayOfWeekWebtoons, genreWebtoons);

		return webtoon.getId();
	}

	public Map<DayOfWeek, List<WebtoonSchema>> readWebtoons(SearchWebtoon search) {
		return webtoonReader.search(search).stream()
			.collect(groupingBy(DayOfWeekWebtoon::getDayOfWeek,
				mapping(dayOfWeekWebtoon -> WebtoonSchema.from(dayOfWeekWebtoon.getWebtoon()), toList())));
	}

	public WebtoonDetail readWebtoonDetail(Long webtoonId) {
		Webtoon webtoon = webtoonReader.read(webtoonId);
		List<String> dayOfWeeks = webtoonReader.readDayOfWebtoon(webtoon);
		List<Genre> genres = webtoonReader.readGenreOfWebtoon(webtoon);

		return WebtoonDetail.of(webtoon, dayOfWeeks, genres);
	}

	@Transactional
	public void updateWebtoonStatus(List<Long> webtoonIds) {
		webtoonWriter.update(webtoonIds);
	}
}
