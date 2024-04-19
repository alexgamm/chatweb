package chatweb.controller;

import chatweb.entity.Dictionary;
import chatweb.exception.ApiErrorException;
import chatweb.mapper.DictionaryMapper;
import chatweb.model.api.ApiError;
import chatweb.model.api.DictionaryDto;
import chatweb.model.api.GetDictionariesResponse;
import chatweb.repository.DictionaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/codenames/dictionaries")
@RequiredArgsConstructor
public class DictionaryController implements ApiControllerHelper {
    private final DictionaryRepository dictionaryRepository;

    @GetMapping
    public GetDictionariesResponse getDictionaries() throws ApiErrorException {
        List<Dictionary> dictionaries = dictionaryRepository.findAll();
        if (dictionaries.isEmpty()) {
            throw ApiError.notFound("No dictionaries found").toException();
        }
        List<DictionaryDto> dictionaryDtos = new ArrayList<>();
        for (Dictionary dictionary : dictionaries) {
            dictionaryDtos.add(DictionaryMapper.INSTANCE.toDictionaryDto(dictionary));
        }
        return new GetDictionariesResponse(dictionaryDtos);
    }

}
