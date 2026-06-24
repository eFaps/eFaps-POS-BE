package org.efaps.pos.controller;

import java.util.List;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosCreditNoteDto;
import org.efaps.pos.dto.ValidateForCreditNoteResponseDto;
import org.efaps.pos.service.CreditNoteService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH)
public class CreditNoteController
{

    private final CreditNoteService creditNoteService;

    public CreditNoteController(final CreditNoteService creditNoteService) {
        this.creditNoteService = creditNoteService;
    }

    @GetMapping(path = { "creditnotes" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "number" })
    public List<PosCreditNoteDto> retrieveCreditNotes(@RequestParam(name = "number") final String number)
    {
        return creditNoteService.retrieveCreditNotes(number).stream().map(Converter::toDto).toList();
    }


    @GetMapping(path = { "creditnotes/{ident}/redeem-validity" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidateForCreditNoteResponseDto redeemValidity(@PathVariable("ident") final String ident)
    {
        return creditNoteService.redeemValidity(ident);
    }
}
