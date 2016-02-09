#ifndef STRING2_H
#define STRING2_H

#include <stdint.h>
#include <stdbool.h>

uint32_t string_split(const char *str, const char *delim, char ***buf_addr);
bool string_have_extension(const char *filename, const char *extension);
char *str_replace_char(const char *str, char from, char to);

#endif //STRING2_H
