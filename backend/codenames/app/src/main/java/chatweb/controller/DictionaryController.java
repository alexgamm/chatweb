package chatweb.controller;

import chatweb.exception.ApiErrorException;
import chatweb.model.api.ApiError;
import chatweb.model.api.DictionaryDto;
import chatweb.model.api.GetDictionariesResponse;
import chatweb.repository.DictionaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/codenames/dictionaries")
@RequiredArgsConstructor
public class DictionaryController implements ApiControllerHelper {
    private final DictionaryRepository dictionaryRepository;

    @GetMapping
    public GetDictionariesResponse getDictionaries() throws ApiErrorException {
        List<DictionaryDto> dictionaries = dictionaryRepository.findAllDto();
        if (dictionaries.isEmpty()) {
            throw ApiError.notFound("No dictionaries found").toException();
        }
        return new GetDictionariesResponse(dictionaries);
    }
}
