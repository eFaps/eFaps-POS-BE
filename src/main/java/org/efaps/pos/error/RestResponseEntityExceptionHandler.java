/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.efaps.pos.error;

import org.efaps.pos.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
    extends ResponseEntityExceptionHandler
{

    private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<Object> handleNotFoundException(final Exception ex,
                                                          final WebRequest request)
    {
        return new ResponseEntity<>(ErrorResponseDto.builder()
                        .withMessage(ex.getMessage())
                        .build(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ PreconditionException.class })
    public ResponseEntity<Object> handlePreconditionException(final Exception ex,
                                                              final WebRequest request)
    {
        return new ResponseEntity<>(ErrorResponseDto.builder()
                        .withMessage(ex.getMessage())
                        .build(), new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler({ BadCredentialsException.class })
    public ResponseEntity<Object> handleBadCredentialsException(final Exception ex,
                                                                final WebRequest request)
    {
        LOG.error("handle BadCredentialsException Exception", ex);
        return new ResponseEntity<>(ErrorResponseDto.builder()
                        .withMessage(ex.getMessage())
                        .build(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleGenericException(final Exception ex,
                                                         final WebRequest request)
    {
        LOG.error("handle Generic Exception", ex);
        return new ResponseEntity<>(ErrorResponseDto.builder()
                        .withMessage(ex.getMessage())
                        .build(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
